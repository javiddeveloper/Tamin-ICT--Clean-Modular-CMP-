# Code Review Process

This folder holds review reports. Every open merge request has one file:

```
review/
├── README.md        ← this file — the process
├── TEMPLATE.md      ← skeleton for a new report
└── MR-<id>.md       ← one file per open MR
```

The file name is exactly `MR-<number>.md` — `MR-161.md`, `MR-164.md`. The number is the one in the merge request URL:
`https://ci.tamin.ir/base/development/finance/tatmin-kmp/-/merge_requests/164`

**The report lives on the MR's source branch, not on `develop`.** That way the developer sees it next to their code with no extra step, and when the MR closes the file goes with it.

> ⚠️ **Reports are written in English** — headings, item descriptions, the summary table and the revision log. This `README.md` is the only file here that is not a report.

---

## The full cycle

```
Round 1  ─ reviewer ─→  report committed to the source branch
                            ↓
              developer applies fixes and pushes
                            ↓
Round 2  ─ reviewer ─→  the same file is updated, item by item
                            ↓
              … until every item is closed
                            ↓
            reviewer deletes the file and pushes
                            ↓
              merge happens through GitLab
```

---

## Step 1 — Preparation

⚠️ **Do not disturb the working tree.**

The safe approach is a separate worktree, so the main project folder and the user's uncommitted changes stay untouched:

```powershell
git fetch origin "refs/merge-requests/<id>/head:mr-<id>" --force
git worktree add ../review-mr-<id> mr-<id>
```

If a worktree is not possible for some reason, confirm `git status` is clean first. **Never run `checkout` on a working tree that has uncommitted changes.**

To find the branch behind an MR number:

```powershell
git ls-remote origin "refs/merge-requests/<id>/head"
# then match the sha against the output of: git ls-remote --heads origin
```

Before pushing, confirm the sha of `mr-<id>` matches the sha of that branch on the remote. If it does not, the branch has moved and you need to fetch again.

---

## Step 2 — Writing the report (Round 1)

Copy `TEMPLATE.md` to `review/MR-<id>.md` and fill it in. **The report is written in English.**

**Diff scope** — always against the merge-base, not the tip of `develop`:

```powershell
$base = git merge-base origin/develop mr-<id>
git diff --stat $base..mr-<id>
```

**Writing rules:**

- Every item must be **verifiable**: file path, line number, and exactly what should change.
- Give each item a concrete failure scenario, not a general remark. "This may cause problems" is not a finding.
- Keep the tone descriptive, not judgemental. The subject is the code, not its author.
- If you are not certain, use `⬜ needs confirmation` rather than recording it as a definite bug.
- If a problem predates this MR and was only carried forward, say so explicitly.
- Do not leave "What was done well" empty when there is genuinely something there.
- **Record the reviewed commit in the header.** Without it the next round does not know where to diff from.

Then commit and push to the same branch:

```powershell
git add review/MR-<id>.md
git commit -m "docs(review): add review report for MR !<id>"
git push origin HEAD:<source-branch-name>
```

---

## Step 3 — Re-review (Round 2 onward)

```powershell
git fetch origin "refs/merge-requests/<id>/head:mr-<id>" --force
git diff <previous-round-commit>..mr-<id>          # only what changed
```

Go **item by item**. Update the Status column in the summary table:

| Symbol | Meaning |
|---|---|
| `⬜ open` | not addressed yet |
| `✅ fixed` | checked and correct |
| `🔄 needs rework` | attempted but still wrong — explain why |
| `➖ rejected` | the developer gave a reason and it was accepted — record the reason |
| `⬜ needs confirmation` | waiting on an answer about design intent |

Each round adds a row to the **Revision log**.

`✅ fixed` is recorded only after the code has actually been read. The developer's commit message is not evidence.

If a new problem appears in a later round, append it to the end of the list — never renumber existing items, or earlier references become meaningless.

---

## Step 4 — Closing out

When nothing is left in `⬜` or `🔄`:

```powershell
git rm review/MR-<id>.md
git commit -m "docs(review): close out review for MR !<id>"
git push origin HEAD:<source-branch-name>
```

Then clean up the worktree:

```powershell
git worktree remove ../review-mr-<id>
git branch -D mr-<id>
```

⛔ **Merging into `develop` is not the reviewer's job.** Once the report is closed out, just state that the MR is ready. The merge itself happens through GitLab and is performed by a human, so approvals, the CI pipeline and release timing all stay where they belong.

---

## Notes

- If an MR carries another branch inside it (like `!166`, which contains `!164`), say so in the report header — otherwise two parallel reports get written against the same code.
- Do not commit a report to `develop`. If one lands there by mistake, delete it.
- Files in this folder are temporary. If `review/` holds nothing but `README.md` and `TEMPLATE.md`, no MR is waiting for review.
- Posting comments directly on GitLab requires `glab` (installed, but `glab auth login --hostname ci.tamin.ir` has to be run once by the user). Without it, the report travels only as this file.
