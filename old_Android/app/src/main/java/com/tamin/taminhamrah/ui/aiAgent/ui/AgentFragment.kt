package com.tamin.taminhamrah.ui.aiAgent.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat.getColor
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import javax.inject.Inject
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.data.repository.ai.model.ClickableItemModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel
import com.tamin.taminhamrah.databinding.FragmentAiBinding
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DeepLinkData
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIIntent.*
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIIntent.SendPrompt.*
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.ChatAdapter
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.ChatActionListener
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.ChatViewHolderFactory
import com.tamin.taminhamrah.ui.aiAgent.ui.tools.AudioRecorder
import com.tamin.taminhamrah.ui.aiAgent.ui.tools.MediaPlayerManager
import com.tamin.taminhamrah.ui.base.BaseFragmentMVI
import com.tamin.taminhamrah.ui.dialog.MicrophonePermissionGetImageTextProvider
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.detail.ElectronicPrescriptionDetailFragment
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.compression.utils.FileUtil
import com.tamin.taminhamrah.utils.extentions.startInfiniteBounce
import com.tamin.taminhamrah.utils.extentions.stopInfiniteBounce
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.utils.imageAnimator.setLookDirection
import com.tamin.taminhamrah.utils.observeBackStackFromPopFor
import com.tamin.taminhamrah.utils.updater.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale

const val CATEGORY_ITEM = "categoryItem"
const val LOAD_ITEM = "loadItem"
const val IS_LAW_SEARCH = "IS_LAW_SEARCH"
const val START_NEW_CHAT = "START_NEW_CHAT"
const val CATEGORY_ITEM_EDIT = "categoryItemToEdit"


@AndroidEntryPoint
class AgentFragment :
    BaseFragmentMVI<FragmentAiBinding, AgentViewModel, AgentContracts.AIState, AgentContracts.AIEvent>(),
    ChatActionListener {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override val viewModel: AgentViewModel by viewModels()
    val mediaPlayerManager by lazy { MediaPlayerManager() }
    val audioRecorder by lazy { AudioRecorder() }
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var isRecordAudioPermissionGranted = false
    private var countDownTimer: CountDownTimer? = null
    private var filePath: String? = null
    private val recordingDurationMillis: Long = 20000
    private val countDownIntervalMillis: Long = 1000
    private var isAutoScrollEnabled = true
    private var isUserScrolling = false
    private var isProgrammaticScroll = false

    private var pendingFieldId: String? = null
    private var pendingPosition: Int = -1
    private var pendingDocTypeId: String? = null
    private var pendingMessageId: String? = null

    private val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUriString = result.data?.extras?.getString(Constants.IMAGE_URI)
                val imageUri = imageUriString?.toUri()

                val fieldId = pendingFieldId
                val position = pendingPosition
                val messageId = pendingMessageId

                if (imageUri != null && fieldId != null && (position != -1 || messageId != null)) {
                    val path = FileUtil.getPath(requireContext(), imageUri)
                    if (path != null) {
                        val body = PickImageUtils.getImageBody(path)
                        viewModel.processIntent(
                            UploadFormDocument(
                                body,
                                fieldId,
                                pendingDocTypeId,
                                position,
                                messageId
                            )
                        )
                    }
                }
                pendingFieldId = null
                pendingPosition = -1
                pendingDocTypeId = null
                pendingMessageId = null
            }
        }

    private val chatAdapter: ChatAdapter by lazy {
        val factory = ChatViewHolderFactory(this, childFragmentManager)
        ChatAdapter(factory)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAiBinding.inflate(inflater, container, false)


    override fun onViewBindingCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewBindingCreated(view, savedInstanceState)
        setupRecyclerView()
        setupUi()
        observeBackStack()

    }

    private fun setupUi() {
        binding?.apply {
            binding?.editTextMessage?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    sendTextMessage()
                    true
                } else {
                    false
                }
            }
            buttonSend.setOnClickListener {
                val text = binding?.editTextMessage?.text.toString().trim()
                if (text.isNotEmpty()) {
                    sendTextMessage()
                } else {
                    sendVoiceMessage(mediaPlayerManager.duration)
                }
            }
            btnEndList.setOnClickListener {
                isUserScrolling = false
                isAutoScrollEnabled = true
                toggleEndListButton(false)
                scrollToBottom(force = true)
            }
            imgHistory.setOnClickListener {
                viewModel.processIntent(OpenBottomSheetHistory)
            }
            imageBack.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                viewModel.processIntent(OnBackPress)
            }
            buttonStop.setOnClickListener {
                if (viewModel.state.value.chatType == PromptType.Generating) {
                    viewModel.processIntent(StopMessageText)
                } else {
                    viewModel.processIntent(StopMessageVoice)
                }
            }
            buttonRecord.setOnClickListener {
                if (recordAudio()) {
                    viewModel.processIntent(OnMicClick(filePath))
                }
            }
            layoutVoiceRecorder.buttonDeleteVoice.setOnClickListener {
                viewModel.processIntent(OnDeletedVoice)
            }
            layoutVoiceRecorder.buttonPlayPause.setOnClickListener {
                when (viewModel.state.value.chatType) {
                    PromptType.Voice.Playing -> {
                        viewModel.processIntent(OnPauseVoice)
                    }

                    PromptType.Voice.Paused -> {
                        viewModel.processIntent(OnPlayVoice)
                    }

                    else -> {
                        viewModel.processIntent(OnPlayVoice)
                    }
                }
            }

            tvToolbarTitle.text = getString(R.string.ai_lows_assistant)
            fabAnimation()
            inputTextListener()
        }

    }

    override fun renderState(state: AgentContracts.AIState) {
        binding?.apply {
            chatAdapter.submitList(state.chatItems) {}
            lottieLoading.isVisible = state.isLoading


            when (state.chatType) {
                PromptType.Text -> {
                    setRecordButtonVisibility(VISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = INVISIBLE
                    buttonStop.visibility = INVISIBLE
                    editTextMessage.hint = getString(R.string.write_your_message)
                    editTextMessage.isEnabled = true
                    editTextMessage.isGone = false
                    layoutVoiceRecorder.recordeContainer.isGone = true
                    setLookDirection(fabLawAi, requireContext(), false)
                    cancelCountdownTimer()
                }

                PromptType.Generating -> {
                    setRecordButtonVisibility(INVISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = INVISIBLE
                    buttonStop.visibility = VISIBLE
                    setLookDirection(fabLawAi, requireContext(), true)
                    editTextMessage.isEnabled = false
                    editTextMessage.isGone = false
                    editTextMessage.hint = getString(R.string.generating)
                    editTextMessage.text?.clear()
                    layoutVoiceRecorder.apply {
                        buttonPlayPause.isGone = true
                        buttonDeleteVoice.isGone = true
                        audioRecorder.isGone = true
                        audioPlayer.isGone = true
                        buttonPlayPause.setImageResource(R.drawable.ic_play)
                        layoutVoiceRecorder.txtCountDown.isGone = true
                    }
                }

                PromptType.Voice.Recording -> {
                    buttonStop.visibility = VISIBLE
                    setRecordButtonVisibility(INVISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = INVISIBLE
                    layoutVoiceRecorder.apply {
                        recordeContainer.isGone = false
                        audioPlayer.isGone = true
                        audioRecorder.isGone = false
                        buttonPlayPause.isGone = true
                        buttonDeleteVoice.isGone = true
                        layoutVoiceRecorder.txtCountDown.isGone = false
                    }
                    editTextMessage.isGone = true
                }

                PromptType.Voice.Stopped -> {
                    buttonStop.visibility = INVISIBLE
                    setRecordButtonVisibility(INVISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = VISIBLE
                    editTextMessage.isGone = true
                    audioRecorder.stopRecording()
                    cancelCountdownTimer()
                    loadMainAudio { }
                    layoutVoiceRecorder.apply {
                        buttonPlayPause.isGone = false
                        buttonDeleteVoice.isGone = false
                        audioRecorder.isGone = true
                        audioPlayer.isGone = false
                        buttonPlayPause.setImageResource(R.drawable.ic_play)
                        layoutVoiceRecorder.txtCountDown.isGone = true
                    }
                    viewModel.processIntent(VoiceIdle)
                }

                PromptType.Voice.Deleted -> {
                    setRecordButtonVisibility(VISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = INVISIBLE
                    buttonStop.visibility = INVISIBLE
                    layoutVoiceRecorder.apply {
                        buttonPlayPause.isGone = true
                        buttonDeleteVoice.isGone = true
                        audioRecorder.isGone = true
                        audioPlayer.isGone = true
                        buttonPlayPause.setImageResource(R.drawable.ic_play)
                        layoutVoiceRecorder.txtCountDown.isGone = true
                    }
                    editTextMessage.isGone = false
                    editTextMessage.hint = getString(R.string.write_your_message)
                    editTextMessage.isEnabled = true
                    setLookDirection(fabLawAi, requireContext(), false)
                    audioRecorder.stopRecording()
                    cancelCountdownTimer()
                    state.filePath?.let { path ->
                        val file = File(path)
                        file.delete()
                    }
                    binding?.layoutVoiceRecorder?.audioPlayer?.progress = 0f
                }

                PromptType.Voice.Playing -> {
                    playMainAudio()
                    layoutVoiceRecorder.apply {
                        buttonPlayPause.setImageResource(R.drawable.ic_pause)
                    }
                }

                PromptType.Voice.Paused -> {
                    pauseMainAudio()
                    layoutVoiceRecorder.apply {
                        buttonPlayPause.setImageResource(R.drawable.ic_play)
                    }
                }

                PromptType.Voice.Reset -> {
                    editTextMessage.isGone = true
                    removeCallback()
                    cancelCountdownTimer()
                    layoutVoiceRecorder.apply {
                        buttonDeleteVoice.isEnabled = true
                        buttonPlayPause.setImageResource(R.drawable.ic_play)
                        binding?.layoutVoiceRecorder?.audioPlayer?.progress = 0f
                        layoutVoiceRecorder.txtCountDown.isGone = true
                    }
                }

                PromptType.UserTyping -> {
                    setRecordButtonVisibility(INVISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = VISIBLE
                    buttonStop.visibility = INVISIBLE
                    editTextMessage.isEnabled = true
                    cancelCountdownTimer()
                }

                PromptType.StopGenerating -> {
                    setRecordButtonVisibility(VISIBLE, state.isVoiceEnabled)
                    buttonSend.visibility = INVISIBLE
                    buttonStop.visibility = INVISIBLE
                    editTextMessage.hint = getString(R.string.write_your_message)
                    editTextMessage.isEnabled = true
                    setLookDirection(fabLawAi, requireContext(), false)
                    cancelCountdownTimer()
                    chatAdapter.stopCurrentTyping()
                }

                PromptType.Voice.Idle -> {}
            }
            when (state.voiceListState) {

                is VoiceListState.PauseListVoice -> {
                    state.chatType.apply {
                        stopListAudio()
                    }
                }

                is VoiceListState.PlayListVoice -> {
                    state.voiceListState.apply {
                        playListAudio(this.model, this.itemPosition)
                    }
                }

                is VoiceListState.ListProgressUpdating -> {}
                VoiceListState.ListVoiceReset -> {
                    stopListAudio()
                }

                VoiceListState.VoiceListIdle -> {}
            }
        }
    }

    override fun handleEvent(event: AgentContracts.AIEvent) {
        when (event) {
            is AgentContracts.AIEvent.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            is AgentContracts.AIEvent.BackPressed -> {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                handler.postDelayed({
                    if (isAdded) {
                        requireActivity().onBackPressed()
                    }
                }, 100)
            }

            is AgentContracts.AIEvent.NavigateToHistory -> {
                goToHistoryBottomSheet(event.isLawRequest)
            }

            is AgentContracts.AIEvent.NavigateToEdit -> {
                goToBottomSheetEdit(event)
            }

            is AgentContracts.AIEvent.ClickableClick -> {
                when (event.itemAction) {
                    is AgentActionContent.DeepLink -> {
                        var id: Int?
                        var bundle: DeepLinkData?
                        when (event.itemAction.deepLinkData) {
                            is DeepLinkData.Patient -> {
                                id =
                                    R.id.ai_fragment_to_electronicPrescriptionDetailFragment
                                bundle = event.itemAction.deepLinkData
                            }
                        }
                        handlePageDestination(id, createToolbarBundlePatient(bundle))
                    }

                    is AgentActionContent.Web -> {
                        lunchUrl(event.itemAction.url)
                    }

                    is AgentActionContent.SendPrompt -> {
                        viewModel.processIntent(MessageType(event.itemAction.prompt))
                    }

                    is AgentActionContent.LocalDeepLink -> {
                        val uriString = event.itemAction.uri
                        if (isContractDeepLink(uriString) && !isContractServiceActive(uriString)) {
                            Toast.makeText(requireContext(), "این خدمت در حال حاضر غیرفعال می‌باشد.", Toast.LENGTH_SHORT).show()
                        } else {
                            val request = NavDeepLinkRequest.Builder
                                .fromUri(uriString.toUri())
                                .build()
                            findNavController().navigate(request)
                        }
                    }

                    is AgentActionContent.Dial -> {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = "tel:${event.itemAction.phoneNumber}".toUri()
                        startActivity(intent)
                    }

                    is AgentActionContent.EditMobile -> {
                        // Handled in ViewModel
                    }

                    is AgentActionContent.OccurrenceReportGet -> {
                        // Handled in ViewModel
                    }

                    is AgentActionContent.AddAccountNumber -> {

                    }

                    is AgentActionContent.DisplayReport -> {

                    }

                    is AgentActionContent.CancelDependent -> {
                        viewModel.processIntent(OnClickableClick(event.itemAction))
                    }

                    is AgentActionContent.InquiryEducation -> {

                    }

                    is AgentActionContent.WeddingPresent -> {
                        viewModel.processIntent(AgentContracts.AIIntent.OnClickableClick(event.itemAction))
                    }
                }
            }
        }
    }


    fun goToBottomSheetEdit(event: AgentContracts.AIEvent.NavigateToEdit) {
        AgentFragmentDirections.aiFragmentToAiEditCategoryBottomSheet(event.item)
            .also { action ->
                findNavController().navigate(action)
            }
    }

    private fun toggleEndListButton(show: Boolean) {
        binding?.btnEndList?.apply {
            if (show && visibility != VISIBLE) {
                visibility = VISIBLE
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up_in))
            } else if (!show && isVisible) {
                val anim = AnimationUtils.loadAnimation(context, R.anim.slide_down_out)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        visibility = GONE
                    }
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                startAnimation(anim)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = false
            reverseLayout = false
        }

        binding?.recyclerViewChat?.apply {
            adapter = chatAdapter
            this.layoutManager = layoutManager
            itemAnimator = null

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserScrolling = true
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isUserScrolling = false
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (isProgrammaticScroll) return

                    if (dy < 0) {
                        isAutoScrollEnabled = false
                    }
                    if (dy < -10) {
                        chatAdapter.skipTypingAnimation()
                        viewModel.processIntent(SkipTypingAnimation)
                    }
                    else if (dy > 0) {
                        val isAtBottom = !recyclerView.canScrollVertically(1)
                        if (isAtBottom) {
                            isAutoScrollEnabled = true
                        }
                    }
                    toggleEndListButton(!isAutoScrollEnabled)
                }
            })

            chatAdapter.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (isAutoScrollEnabled) {
                            scrollToBottom(force = true)
                        }
                    }
                }
            )
        }
    }

    private fun scrollToBottom(force: Boolean = false) {
        if (!force && !isAutoScrollEnabled) return

        binding?.recyclerViewChat?.post {
            val count = chatAdapter.itemCount
            if (count > 0) {
                isProgrammaticScroll = true
                val lastIndex = count - 1
                binding?.recyclerViewChat?.scrollToPosition(lastIndex)
                binding?.recyclerViewChat?.post {
                    binding?.recyclerViewChat?.scrollBy(0, Int.MAX_VALUE)
                    isProgrammaticScroll = false
                }
            }
        }
    }

    private fun autoScrollDuringTyping() {
        if (!isAutoScrollEnabled || isUserScrolling) return

        binding?.recyclerViewChat?.post {
            isProgrammaticScroll = true
            binding?.recyclerViewChat?.scrollBy(0, Int.MAX_VALUE)
            binding?.recyclerViewChat?.post {
                isProgrammaticScroll = false
            }
        }
    }


    private fun sendTextMessage() {
        val message = binding?.editTextMessage?.text.toString().trim()
        if (message.isNotEmpty()) {
            isUserScrolling = false
            isAutoScrollEnabled = true
            viewModel.processIntent(MessageType(message))
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            binding?.editTextMessage?.text?.clear()
            scrollToBottom(force = true)
        }
    }


    private fun sendVoiceMessage(duration: Int) {
        isUserScrolling = false
        isAutoScrollEnabled = true
        viewModel.processIntent(VoiceType(duration))
        scrollToBottom(force = true)
    }

    private fun goToHistoryBottomSheet(state: Boolean) {
        AgentFragmentDirections
            .aiFragmentToAiCategoryBottomSheet(
                isLawSearch = state
            ).also { action ->
                findNavController().navigate(action)
            }
    }


    private fun observeBackStack() {
        observeBackStackFromPopFor<Boolean>(IS_LAW_SEARCH) { isLawSearch ->
            viewModel.processIntent(UpdateRequestState(isLawSearch))
        }
        observeBackStackFromPopFor<AiHistoryCategory>(CATEGORY_ITEM) {
            viewModel.processIntent(SetCategoryItem(it))
        }
        observeBackStackFromPopFor<AiHistoryCategory>(CATEGORY_ITEM_EDIT) {
            viewModel.processIntent(UpdateCategoryItem(it))
        }
        observeBackStackFromPopFor<String>(LOAD_ITEM) { sessionId ->
            mediaPlayerManager.releaseMediaPlayer()
            audioRecorder.stopRecording()
            viewModel.processIntent(LoadSession(sessionId))
        }
        observeBackStackFromPopFor<Boolean>(START_NEW_CHAT) { shouldStartNewChat ->
            if (shouldStartNewChat) {
                mediaPlayerManager.releaseMediaPlayer()
                audioRecorder.stopRecording()
                viewModel.processIntent(StartNewChat)
            }
        }
    }

    private fun fabAnimation() {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 1000L
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            addListener(object : androidx.transition.Transition.TransitionListener {
                override fun onTransitionStart(transition: androidx.transition.Transition) {}
                override fun onTransitionEnd(transition: androidx.transition.Transition) {
                    binding?.fabLawAi?.startInfiniteBounce(distance = 15f, durationMs = 2500L)
                }
                override fun onTransitionCancel(transition: androidx.transition.Transition) {}
                override fun onTransitionPause(transition: androidx.transition.Transition) {}
                override fun onTransitionResume(transition: androidx.transition.Transition) {}
            })
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 1000L
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            addListener(object : androidx.transition.Transition.TransitionListener {
                override fun onTransitionStart(transition: androidx.transition.Transition) {
                    binding?.fabLawAi?.stopInfiniteBounce()
                }
                override fun onTransitionEnd(transition: androidx.transition.Transition) {}
                override fun onTransitionCancel(transition: androidx.transition.Transition) {}
                override fun onTransitionPause(transition: androidx.transition.Transition) {}
                override fun onTransitionResume(transition: androidx.transition.Transition) {}
            })
        }
    }


    private fun inputTextListener() {
        binding?.editTextMessage?.doOnTextChanged { text, _, _, _ ->
            if (viewModel.state.value.chatType == PromptType.Generating) return@doOnTextChanged
            text?.let { txt ->
                val isVoiceEnabled = viewModel.state.value.isVoiceEnabled
                if (txt.isEmpty()) {
                    setRecordButtonVisibility(VISIBLE, isVoiceEnabled)
                    binding?.buttonSend?.visibility = INVISIBLE
                    binding?.buttonStop?.visibility = INVISIBLE
                } else {
                    setRecordButtonVisibility(INVISIBLE, isVoiceEnabled)
                    binding?.buttonSend?.visibility = VISIBLE
                    binding?.buttonStop?.visibility = INVISIBLE
                }
            }
        }
    }

    override fun onActionClick(action: AgentActionContent, position: Int) {
        viewModel.processIntent(OnClickableClick(action))
    }

    override fun onItemClick(item: ClickableItemModel) {
    }

    override fun onTypingComplete(item: TypingAnimatable) {
        viewModel.processIntent(OnTypingComplete(item))
    }

    override fun onPlayClick(
        model: VoiceModel,
        position: Int
    ) {
        viewModel.processIntent(OnPlayVoiceInList(model, position))
    }

    override fun onPauseClick(
        model: VoiceModel,
        position: Int
    ) {
        viewModel.processIntent(OnPauseVoiceInList(model, position))
    }

    override fun onRetryClick() {
        viewModel.processIntent(OnRetryClick)
    }

    override fun onChipClicked(prompt: String) {
        viewModel.processIntent(StopMessageText)
        viewModel.processIntent(MessageType(prompt))
        isUserScrolling = false
        isAutoScrollEnabled = true
        scrollToBottom(force = true)
    }

    override fun onTypingProgress() {
        if (!isAutoScrollEnabled || isUserScrolling) return
        autoScrollDuringTyping()
    }


    fun loadListAudio(item: VoiceModel, itemPosition: Int) {
        mediaPlayerManager.loadAudio(
            item.path,
            {
//                updateSeekBar { progress ->
//                    mViewModel.updateVoiceSeekBarInList(position, progress)
//                }
                updateSeekBar {
                    viewModel.processIntent(
                        VoiceListProgressUpdated(
                            it,
                            item,
                            itemPosition
                        )
                    )

                }

                mediaPlayerManager.resumeAudio()
//                mViewModel.onVoicePlayInList(position)
            },
            {
                viewModel.processIntent(
                    OnPauseVoiceInList(
                        item,
                        itemPosition,
                    )
                )

//                mViewModel.onVoicePlayCompleteInList(position)
            },
            {
//                mViewModel.updateChatType(Voice.Reset)
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
    }

    fun playListAudio(voice: VoiceModel, itemPosition: Int) {
//        if (voice.path == mediaPlayerManager.currentAudioPath && !mediaPlayerManager.isPlaying) {
//        removeCallback()
        loadListAudio(voice, itemPosition)

//            mediaPlayerManager.resumeAudio()
//        } else if (mediaPlayerManager.currentAudioPath != null && mediaPlayerManager.currentAudioPath != voice.path) {
//            mediaPlayerManager.stopAudio()
//            viewModel.processIntent(AgentContracts.AIIntent.ResetListVoice(voice, itemPosition))
//            loadListAudio(voice, itemPosition)
//        } else {
//            mediaPlayerManager.stopAudio()
//        }

    }

    fun stopListAudio() {
        removeCallback()
        mediaPlayerManager.pauseAudio()
        viewModel.processIntent(VoiceListIdle)
    }

    private fun previewAudio(audioPath: String?, audioDuration: Int) {
        val audioPath = audioPath ?: return
        binding?.layoutVoiceRecorder?.audioPlayer?.apply {
            maxProgress = audioDuration.toFloat()
            setSampleFrom(audioPath)
            onProgressChanged = object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayerManager.seekTo(progress.toInt())
                    }
                }
            }
        }
    }

    private fun updateSeekBar(onProgressChange: (Int) -> Unit) {
        runnable = Runnable {
            mediaPlayerManager.let {
                if (it.isPlaying) {
                    onProgressChange(it.currentPosition)
                    handler.postDelayed(runnable, 100)
                } else {
                    handler.removeCallbacks(runnable)
                }
            }
        }
        handler.postDelayed(runnable, 0)
    }

    fun playMainAudio() {
        if (viewModel.state.value.filePath == mediaPlayerManager.currentAudioPath) {
            binding?.layoutVoiceRecorder?.buttonPlayPause?.setImageResource(R.drawable.ic_pause)
            mediaPlayerManager.resumeAudio()
            updateSeekBar {
                binding?.layoutVoiceRecorder?.audioPlayer?.progress =
                    it.toFloat()
            }
        } else {
            mediaPlayerManager.pauseAudio()
//            mViewModel.onVoiceStopInListFromMain()
            removeCallback()
            binding?.layoutVoiceRecorder?.apply {
                loadMainAudio {
                    updateSeekBar {
                        audioPlayer.progress =
                            it.toFloat()
                    }
                    mediaPlayerManager.resumeAudio()
                    buttonPlayPause.setImageResource(R.drawable.ic_pause)
                }
            }

        }

    }


    fun pauseMainAudio() {
        mediaPlayerManager.pauseAudio()
        binding?.layoutVoiceRecorder?.buttonPlayPause?.setImageResource(R.drawable.ic_play)
    }


    fun loadMainAudio(onPrepared: () -> Unit) {
        mediaPlayerManager.loadAudio(
            viewModel.state.value.filePath,
            {
                binding?.layoutVoiceRecorder?.buttonPlayPause?.isEnabled = true
                previewAudio(viewModel.state.value.filePath, it)
                onPrepared()
            },
            {
                viewModel.processIntent(ResetVoice)
            },
            {
                viewModel.processIntent(ResetVoice)
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
    }


    private fun recordAudio(): Boolean {
        val permissionUtils = PermissionUtils()
        if (permissionUtils.isPermissionGranted(
                Manifest.permission.RECORD_AUDIO,
                requireContext().applicationContext
            )
        ) {
            audioRecorder.recordAudio(
                requireContext().applicationContext.cacheDir.absolutePath,
                { },
                {
                    binding?.layoutVoiceRecorder?.audioRecorder?.update(it)
                },
                {
                    filePath = it
                },
                {
                },
                {
                    binding?.layoutVoiceRecorder?.audioRecorder?.recreate()
                })
            startCountdownTimer()
            return true
        } else {
            requestRecordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return false
        }
    }

    private fun startCountdownTimer() {
        cancelCountdownTimer()
        binding?.layoutVoiceRecorder?.txtCountDown?.apply {
            text = formatTime(recordingDurationMillis / 1000)
            isGone = false
        }

        countDownTimer = object : CountDownTimer(recordingDurationMillis, countDownIntervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.layoutVoiceRecorder?.apply {


                    audioRecorder.chunkColor =
                        if (millisUntilFinished < 5000) Color.RED else getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )

                    txtCountDown.text =
                        formatTime(millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                binding?.layoutVoiceRecorder?.apply {
                    txtCountDown.text = formatTime(0)
                    txtCountDown.isGone = true
                    audioRecorder.chunkColor =
                        getColor(requireContext(), R.color.colorPrimary)

                }
                viewModel.processIntent(StopMessageVoice)
            }
        }.start()
    }

    private fun cancelCountdownTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    private val requestRecordAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                isRecordAudioPermissionGranted = true
            } else {
                val permissionDialog = PermissionMessageDialog()
                permissionDialog.showPermissionDialog(
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        Manifest.permission.RECORD_AUDIO
                    ),
                    permissionTextProvider = MicrophonePermissionGetImageTextProvider(),
                    onCancelClicked = {},
                    onOkClicked = {},
                )
                permissionDialog.createDialog().show(childFragmentManager, "permissionDialog")
                isRecordAudioPermissionGranted = false
            }
        }

    override fun onStart() {
        super.onStart()
        val callback: CustomTabsCallback = object : CustomTabsCallback() {
            override fun onRelationshipValidationResult(
                relation: Int, requestedOrigin: Uri,
                result: Boolean, extras: Bundle?
            ) {
                // Can launch custom tabs intent after session was validated as the same origin.
            }
        }
        // Set up a connection that warms up and validates a session.

        // Set up a connection that warms up and validates a session.
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                // Create session after service connected.
                mSession = client.newSession(callback)
                client.warmup(0)
                // Validate the session as the same origin to allow cross origin headers.


            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerManager.stopAudio()
        removeCallback()
        audioRecorder.stopRecording()
        cancelCountdownTimer()
    }

    private fun removeCallback() {
        if (::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onStop() {
        super.onStop()
        mConnection = null
        mSession = null
        mediaPlayerManager.releaseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallback()
        mediaPlayerManager.releaseMediaPlayer()
        audioRecorder.stopRecording()
    }

    override fun onDestroyView() {
        binding?.fabLawAi?.stopInfiniteBounce()
        binding?.btnEndList?.clearAnimation()
        binding?.recyclerViewChat?.clearOnScrollListeners()
        super.onDestroyView()
    }

    private fun createToolbarBundlePatient(item: DeepLinkData.Patient) =
        Bundle().apply {
            putLong(
                ElectronicPrescriptionDetailFragment.ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION,
                item.noteHeadEprescID ?: 0L
            )
            putString(ElectronicPrescriptionDetailFragment.ARG_REQUEST_TYPE, item.prescType)
            putString(ElectronicPrescriptionDetailFragment.PRES_TYPE, item.prescType)
            putString(ElectronicPrescriptionDetailFragment.ARG_NATIONAL_CODE, item.nationalCode)
            putString(
                ElectronicPrescriptionDetailFragment.ARG_CHILD_NATIONAL_CODE,
                item.childNationalCode
            )
            putString(ElectronicPrescriptionDetailFragment.ARG_FLAG_SATA, item.flagSata)
            putString(Constants.TOOLBAR_TITLE, item.prescName)
            putString(
                Constants.TOOLBAR_SUBTITLE,
                "${getString(R.string.doc_name)} : ${item.docName}"
            )
            putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes ?: 0)
        }

    override fun onFormAction(actionId: String, data: Map<String, Any?>, position: Int, messageId: String?) {
        viewModel.processIntent(FormAction(actionId, data, position, messageId))
    }

    override fun onUpdateFormData(data: Map<String, Any?>, position: Int, messageId: String?) {
        viewModel.processIntent(UpdateFormData(data, position, messageId))
    }

    override fun onFormDocumentRequest(fieldId: String, position: Int, messageId: String?) {
        pendingFieldId = fieldId
        pendingPosition = position
        pendingMessageId = messageId
        viewModel.getDocumentTypes { docTypes ->
            if (docTypes.isNotEmpty()) {
                openImageTypeMenu(androidx.paging.PagingData.from(docTypes), onResultCallBack = object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        pendingDocTypeId = itemResult.id
                        val intent = Intent(requireActivity(), MultiCustomGalleryUI::class.java)
                        intent.putExtra(Constants.TEMPID, itemResult.id.toString())
                        intent.putExtra(Constants.REQUEST_CODE_TAG, Constants.REQUEST_LUNCHER)
                        resultImageLaunch.launch(intent)
                    }
                })
            } else {
                val intent = Intent(requireActivity(), MultiCustomGalleryUI::class.java)
                intent.putExtra(Constants.TEMPID, fieldId)
                intent.putExtra(Constants.REQUEST_CODE_TAG, Constants.REQUEST_LUNCHER)
                resultImageLaunch.launch(intent)
            }
        }
    }

    private fun setRecordButtonVisibility(visibility: Int, isVoiceEnabled: Boolean) {
        binding?.buttonRecord?.visibility = if (isVoiceEnabled) visibility else GONE
    }
    override fun onDisplayReport(action: AgentActionContent) {
        val title = (action as? AgentActionContent.DisplayReport)?.title
        viewModel.processIntent(DisplayReportMode(title,action.actionText.toString()))
    }

    private fun isContractDeepLink(uri: String): Boolean {
        return uri.contains("contract_optional") ||
                uri.contains("contract_woman") ||
                uri.contains("contract_freelance") ||
                uri.contains("contract_student")
    }

    private fun isContractServiceActive(uri: String): Boolean {
        val serviceId = when {
            uri.contains("contract_optional") -> 37
            uri.contains("contract_woman") -> 36
            uri.contains("contract_freelance") -> 33
            uri.contains("contract_student") -> 34
            else -> return true
        }
        return try {
            val servicesResponse = preferenceManager.getServices()
            val serviceItem = servicesResponse.data?.find { it.id == serviceId }
            serviceItem?.active ?: false
        } catch (e: Exception) {
            false
        }
    }
}
