package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_aparat
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_logo

const val userProfileBase64 = "/9j/4AAQSkZJRgABAQECWAJYAAD/2wBDACAWGBwYFCAcGhwkIiAmMFA0MCwsMGJGSjpQdGZ6eHJmcG6AkLicgIiuim5woNqirr7EztDOfJri8uDI8LjKzsb/2wBDASIkJDAqMF40NF7GhHCExsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsb/wAARCAEsAOEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9o_8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDoKKKKACiiigAoopCQOtAC0UisG6UtABRSbhux3o3DdjvQAtFFFABRUTzxxnDMM+lN+0puAGTnvQBPRTQynoQadQAUUUUAFFJuGcUtABRRSE4GTQAtFIORS0AFFJmgEEkelAC0UUUAFFFFABSK24UEgdTimqR8x7UAG/npx604+mcGoz6K2fanbWzndzQADC55pGB3ZIyKNh/vUu1v71ADhgjjpS0gGBio5Z0ibDHt0oAcf9YPcUZAdifSs+bUxu+QdD3qjNeyyZwTg0AaFzqax5VeT6iqn9qOHJBJBHes9iWOT+tN+hoGTy3DMOTlictSNdSMuCePSoOScA5pVGDzigCzHfSRj5DgVah1aRVxIN59elZ5Pp+lNx64oA34NSgk4YlW/wBqrm4YzkYNcqCB6VLHcMjAq5GPSgR0pIDAY606s221GNwPNYhh+VX1kV13IQRQA7nPTikblDilBBXOaag+UjtQAoOEB9qFbdxjBpuw4xu4pQCDuY0ALkB8Y5NIP9Y1BIZhjnFKyknIOKAFyOfak3cgeopuxum7g9aUKcgk5xQA+iiigBCAeopaKKAEAA6CloooAKKKQkAZJwKAI7iUQxFz+tc/c3DTyE561a1G789/LjPyDv61SGFGe9AwC8cj60jt0xz9elMLlvpRnHYYoAUq7HJwKTyx3OaXzG6KAPwoz/e4+lACfKvSk+XPY0px6Ypp/E0AKwHakU88nFMzilznsKAHFQORSE47Yoznjv2pH+bBzQAokx3q5aX7w8A5X0NUOQKcr4oEdNbTQ3IynUdRVquatbho5AUbB/Q1vwTiWLeeCOo9KAJqKQciloAQADoKWikBBJHpQAtFFFABRRRQAUituFBIHU4pq4yx7UALv56cetKemM4NRn0Vs+1O2vnO4ZoABhc88VnarcFD5Snk9fpV5wUQsTwvNc/NIZJGkkOWJ6UANXAGScf1pjMD7+1MZtx5NHQZpDAntR9OaTsc0m7jHSmA8DHUnmjcOw/GmcmlzjsaAA/hSZpSw+lN3GgAyDRgZ4pBn/8AVSkeooAaaUHPBoz7Ud+BQAHpTacSPSm+goEPQ8DNX7C9MDFXyYyMfSs9etPBwaAOsjdWjDA8YpVbd7Vi6bcnIhZuP4a2QCDuY9qAFyA+McmkH+sagkFhjkilZTnKnFAC5H5Um4ZA9Rmm7G5G7g9aUKcgk5xQA+iiigBCAeopaKKAEAA6CloooAz9WuPLhEY6v1+lYJPfFW9TnE143OVTgVTB70DEztpC3rQ+AaZkZoAcW9f0o+lJR+NADh0604Zx60wc1OCqigRGAev9KQ+wyacTuPtTuBwKAI9rev5UmPensSfWm4zQMTp6Gm1II89jSmI+hpXHYhzRT2jIpnPSmIevTNBPNIvSkxzQIniYqQQcEciumtZhcW6yeo5+tcshwa19Fmw7wk8H5hQBrgAdBS0gIP4UBsk+1AC0UUUAFFFFABTVbcuaCwXrSLwCSMA0AAY5GRwelR3UoihYltpI4NOxk4UnH8qq6kQlu3zbieMZoAwZGyzHuTzTMjA5pTim5xQMG5FMxTs80hFACcUCkxRQA/dSbs02nqpNAADT1DfSnxwk1aS2PepbKUSusZNTJb+1W0gAqUR1NyrFdIAKkEI9KnCigjFAFGa2BBxVGSHHGK2SKgliDDPencRk7cUnerM0frVY8GqTJaHrgVd0o/6dH+I/SqG6rNhIEu4mPQNTJOmB+cjFIv3moBDMCO3elK5OQSKAFLAAn0pN3zY9qTy+244pQuDknNADqKKKACiiigArN1eNPKEhHzZxWlVTUwDYyZ7dKAObbAPFMPXinv8ASo8GgYYpuadjmlCF8+1ADKVVJqZbdiwXAz1OTVtLXjk59lGKVxqJTSLkDGTVuG1zy35Vcit9o4AAqwsQFTcqxXjhC9BU4QCpNoFBpDGgUuKWigBMU0inUhoAYaY3Snmo3oAqTqMVnSferSm6Gsx+WqokyEFT2+fOTHXcKhFWLSN3mXYMkHP5VRJ1QpaarAjilDZJHpQIWiiigBCOlAAGeOtAAXOKZJKsalncKB1JpiAsF7ijO/A6c9qyLzU/4Yf++jWf9qlLZ8xs/XFJsVzcuhOisUAZT27isW6laSF8scjsamj1KZMAsGHuKdI9vcqQAI5O2OhpbgZ6NlAec06pY7V1YgkA+lW47T5TuPPoKVmXcp7qN9WpLXHI4+tRG3cdqAuRb6N9PEEh/hpy20ncYoC5FvPrUsc7RmpFt/UnNSG16EGgdy3bX4OAfzrbhcSRqytkGuT+zyKeORV6zvpbcgMcjtQB0oJBAJyDSt83HSqsN3HLGGU4NWVIYfK2RQAtFIORS0AFFJmgEEkelAC0UUUAFFFFABSK24UEgdTimqR8x7UAG/npx604+mcGoj6K2fan7WzndzQAArnnigY3ZzkYpNh96Xay8Dkd6AEZ9zAA8VkancCR/KU/KvX3q7dXawIVU5fHasB3aRySck0mNCDpS/WpI4Hf7vWpvsUncjPrSuUkVQvGaD7HmrYsZD6H8aeNPk/2fzouO1yj5jY60u9vU1fFhJ3Kj86kGnEcll/Ki47GYHbPWpRKR3NXfsCDqy/kaX7HH/fH5UuYLGf5jetBlbFaP2OP1/SlFnGe9FxcplCRzTvMkPetX7HEPSlFpGOwouOxneaxHzU3eR/FWp9mSmm3jouOxl+a/rTDK56mtQ26ntUTWoPTFFgsZ/nSD+I0nnSepq+bU9sU37G3ciixNiitw6nmpkuyO9Pa0f2NQvbuvQUWCxaS8Pc8Vaiuw3Q9KxGVs8jFPjkZGBBoCx0scqyLkYPtTtwzgnB9axob3aRk4PvWpFIsih1OQaBD8468ikIywI6CjaM5HBoxkkGgBcgPg+lIP8AWNQSGxnHFKytnI6GgBcjGfWk3YI75pNjcDtmhVOCSQeaAH0UUUAJ3oqN7iOP7zAfWoG1KMfdBb6U7CLu4UbuKzG1Jz91APrUf2+Y9WA/Ciwrhqs3lRYA5fv6VisuCOat3czTKA3XNVN2TUlDloI5zQnIpxpFCdBUkbBGBNRkU0GkUasUivypGKnBrDRmRgV4Iq5Be8Yk/OhMGzS3U13AptvG9wflGF9TWhFZRRYO3LdyadiLkEGlySrmT5f61oxxJCmEH61IuNvFCv82M8CqSFcRWB470pI3YzyaTYO3GaUp82c8CmAueOnFIDmTIHGKNg/I0pIDZzyBQAuRnHfGKTB8zPamlyDkYpC3zZ6UAPzxzxik3YIA6k0mcc96TeCQOuTQA+iiigBvHfpVS5vlhyoOW/lUWo3RjPlpx6mslmLNliSaVykizzKxdjSggEADpURcrgUquSc96k0TLisBT1bdVRpSvapIpgSKkLlyjNM37loL4oGOpuc0m7NIe9ADhUijiohUqY4zSYExO1BVVjz0q0Pmiqo+V8VJBYg4arD1UhPzZ7VYaSpsUmMZuD61XLEGpGbKmqzk0iieG5KHB5FaUcivGGA6+tc+8m1fatGyuN6FT2oAtvSAnbnvTiRtzQhGM54oAaxJGRxQ7fMOcCkkYk5HAp8nKjnFAAcl8dqX7vGOvSiPGeOtID6nkUALkfKOuaXPzEd6aOuc8UvfpQA+iiigBCcA1Uu7r7PGe7HoKnkYKhY8AVz93OZ5mOTjPAoAY7liWY8mmsMj6UE44NA+ZTSGNDcUuNwyDTSOMUE8YFAD8jHFA60xeBk07fQBJ9KcG7VDupwagCSnA8VAz0qvyOetAEvXpUiAjk0xRUuRtoExd5XgGmMxY5NAOTik70CQuM0uRmkziigBy1IsjI4ZTgiolNPU0AdLazieIMD9asLgg+1YOny+XNjPBrejK8+9ACqR0PWhCSMgY5oXGeOaEPzYHIzQAByW46elH3uMdBmjpICeh4oHUnvjFAC5Hy46ZpfvMQfSmjr06U6gAooooATPOKZJIkalmOKVmCgsxwBXNalqBuZCif6tT+ZoAZqGoNdPsztiB6evvVRR8vWmkc05TxSGL9KO2KaThqN1ADvYUECjNGOKAEJxwaVQM9aaRmnrxQAtO3ZpvWloAQ8mnhhimEUlAiyhFOY8VAnIqc9KQEZ6UCkNKDigYHrRSZpBQIdSg+tNozQBMjFXBBxiumtZxPErD6VycRLOAeldLayieFX796ALC8Z5yKFOfX6UisM5/WhSTkDrmgBykscH86Pu9M5oGSc98YoX7xXn60ALkEAdBSZBYH3pcgNnPFIBk5XvQA6iiigBGbaKwtV1Ag+TGeT1IrXun2QsR1xXKTuXndiSTnvQBCMnvTwMClC8ZpN4JxQMTuRS9DRt6mhcdaADFHal3cU0vmgBaO1HUA0vSgBR0paT7uKTcaAHU0mk3GlUZ60CLFsgc81O8GKiidY6klmB6UhlaYYqE9KlkYGoWOKBi0UlLigAooooAmhIVgRXTWkglhV+5FcqOladhc7HCE8GgR0K4570KcnHp6VCHOR+tKrfNu96AJRgkdqXI7e1RhsnB4pfM579OaAHqc4PSlyB6800EAnJzQDlsHpQBJRRRQBS1Byts7DqRXLo27OfWumu7d7hCikBSOc1lLoU2/8A1igegoAw34O0Un1rdbQX35WQD1qRdBTf80hIoGYscDuMqpNWEsZT95StbyaZAnGwnHpUwtohjEakfSgaOf8A7NbGdxpf7OfHBzXSGKMYOxRTvJXHCj8qBnMjSp2+6F/GnjSpgeQB+NdGI8UoQHggUDObGkzHrxTv7Ic8GT9K6LYAOAKQx85oGc/8A2SwGN2acult03CugMfpR5VBmYH9lN2cUh0p+u+t/yvWkMfNAGANMbHLU4aYD1NbgipRHTFYwv7LA70f2YnvW55dHl0DMT+zU9DSHTox3rbeHAqjLIF6mi4GXLAsZ4Gaj6U+Rw8hKmm0xCqauW77ZAfeqWKkhfbIpoEdNbTGeJW74xVheD7+lVbWUTQqR2GKsjgZ65oAkAyTigHkj8aYGycHtSmTB6fSgB68D1paYvPPrT6ACiiigBNuTmilooAKKKKAEIB6ikKAngU6igBhXjFOCgDgUtFABRRRQAUUUUAFFFFABRRRQAUmKWigBMY6VQ1KbyoSAOTxWhWJrEn70KOmKAuZDMCeKaetIeGNHWmMUUZpKTvSAu6fOI5dp6Ma3UYE8e1cuCVIYfSt/S7gzw5P3lNAF8YJI7UA8kfSmeZzx6ZpS+D060APXnnvT6YvPNOoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArB1R99049OK3WbYpOMnFYGoRkSFyOpNA0Z/8Roak6NTm7UDFHSmA5pxPFNA60ALu4xV7TLjyLkKeVbg1QUcc1IPlO4HkUDOq3YOT6UrN8pPqOKZGyzwqy9MUKflOfSgRIuR9KdTFOR9afQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFBOBQBXupfKgdvauYmcyuxNbWqzAW7jueKwh8yZ70DQ3+KnE4pDSMaBgTmlU03PNOHI96ACpAMio6cpoA1dJu/ImCOfkb9K3iAG9e9ckhyK3dMu/PhCOf3i/rQIvKwJwDmnA/NjtTVOWOO1CtmTI6CgB+RnHelpgYZJ/Gn0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFV7qZYIXc9AKsVj6xKcpCOjc0AZM8rzTux6N0FRD5Tg9DT8YGKafm49KBiGmk08D1FNAy2e1IA7VMoG3moqfGQDk0AKVpBSsc05B60AJT45GjcMpwRTM8k0Z5oA3tNu/OiaM/eHINXAQvPauatZjDcJJn5R1roaNvX9aAJQwLYB6UqNlnHpxUYIUknoaVGyxYcjFAEmeMmkDAjINJuO/B4FJvAXgDmgB+evFIThSTSZBbaOwpCQWx2HWgByNkdKdTVYEU6gAooooAKKKKACiiigAooooAKKKKACiiigArCu2E16fRRg1tSsEiaQ9hmuahkLM7nuSaAHvxzTOgpznK0zqKQxBTu1NGcU4nigAUEninNxxSxjBz6UnU5NABjtSU7rSHrQAAcZq9pt15E4Vj8rGqOOMUFtpyOooA68MGGRyDQG5Yn8Kpabc/abcE8MvWrufmI9etAD9wxk8cUFsEDuaRsFcd6QHL49BQAuRuY98Umc7R2xzTcHzN3alPXPfFAD8cc80i9/XFMB7noDS7wBknFAD6KTPpS0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAVr8kWUm3riucB2rgda6S6XdbSAnAxXMTMI/vHBoAR2IWo+TSO+5fQGmq9IZMOBS9ajDU8GgB1LkGmU8UAOHBpDSUooAUUuKaTinKc0AaOjzCK82scK3FdCDlv0Irkhwyv3U5rp7WQTQIwPIH60AWByuOgpAfmweBTMuCct07U4Pllz34oAk3DOO9BbkAdaZuIYKvXGaUPnnHOOlADvYUgIDYzz60mSOnNIDmTgZ460AOHrS0gpaACiiigAooooAKKKKACiiigAooooAKKKKAIntIps+YNwrn7/SlUny+h6V01NZA+R0BoA89mtpoZCrKRSDeB90ivQLnTbe4Ta6DjuK5+98PzoS0ZDL7CgZiK571KrBqt/2PMBl0OfUCmHT5EOCMUDGg04GlNnOn8PFAt5B60AKOlOHWof3ijpQJnU8rQBPUqnFQCTd/DUm8AdKAJ+pAre0mbMQU9RXOxsZDhcZrVsJHtZVMv3T1NIDfDAEjHXtTkPzNxx600MCRtGRSr8xbtzzQMeGycDqOtC87gTzmoWJDYBwaeHO446AUAPyB2pMjeCOe1Rbj0zz1pVfLZzigCXNLTe/FOoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigBpAznvSFQeoyKdRQBGYI3X5kU/UVVm0u2cZC7D6ir9FAHPvovOQwb2qq+kzL91AfyrraKAOO/s26QYEY/Knrpk+MsuPwrsSAeoFIVBHIzTA5WKy2D5jg/SrS2isQOfwrfa3jY8oPypv2SEHIXB9qQGfBbCId6tLGM8Yqz5IHTpR5XoKAIVGCRnBpRyx9TUvk460eSOnamBGBkgY7UoB37u1SeV6Gjyz60wGBck5p4AHSlC8U6mAtFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFRRRRAf//Z"
const val userImage = "D:/Project/KMP/TaminHamrahCMP/core/core-ui/src/commonMain/composeResources/drawable/user_avatar.png"

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToRouteById: (Int) -> Unit = {},
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.sendIntent(ProfileIntent.LoadProfile(userId))
    }

    HandleProfileEvents(
        events = viewModel.events,
        onNavigateToRouteById = onNavigateToRouteById,
        onBackClicked = onBackClicked
    )

    ProfileContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleProfileEvents(
    events: Flow<ProfileEvent>,
    onNavigateToRouteById: (Int) -> Unit,
    onBackClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            ProfileEvent.NavigateBack -> {
                scope.launch {
                    onBackClicked()
                }
            }

            ProfileEvent.NavigateToSettings -> {
                // For now, let's assume destinationId for settings is 100 or something,
                // or we can handle it differently.
                scope.launch {
                    // onNavigateToRouteById(100)
                }
            }
            is ProfileEvent.ShowToast -> {
                // Handle toast
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(
                        model = userProfileBase64,
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (!state.userId.isNullOrEmpty()) {
                        Text(
                            text = state.userId,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }

            item {
                StandardListItem(
                    title = "اطلاعات هویتی",
                    subtitle = "نمایش اطلاعات هویتی و شماره تأمین اجتماعی",
                    icon = painterResource(Res.drawable.ic_tamin_logo),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("اطلاعات هویتی")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "ارتباط فعال با تأمین",
                    subtitle = "وضعیت ارتباط فعال با تأمین اجتماعی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("ارتباط فعال با تأمین")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "مشاهده و ثبت افراد تبعی",
                    subtitle = "مشاهده و ثبت افراد تبعی توسط بیمه شده اصلی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("مشاهده و ثبت افراد تبعی")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "پرونده الکترونیک من",
                    subtitle = "مشاهده مدارک ثبت شده در سیستم",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("پرونده الکترونیک من")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "شماره حساب بانکی",
                    subtitle = "استعلام و ثبت شماره حساب های بانکی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("شماره حساب بانکی")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "تغییر شماره موبایل",
                    subtitle = "جهت شناسایی شما در اپلیکیشن تأمین من",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("تغییر شماره موبایل")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "تنظیمات",
                    subtitle = "مدیریت ظاهر و امنیت برنامه",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("تنظیمات")) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "خروج از حساب کاربری",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick("خروج از حساب کاربری")) }
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreview() {
    PreviewRtlThemeContent {
        ProfileContent(
            state = ProfileUiState(
                userId = "1234567890",
                isLoading = false
            ),
            onIntent = {}
        )
    }
}
