---
tags: [convention, howto]
---

# Code Review

The full, authoritative process lives in **`review/README.md`**. This page is the quick reference.

## In one glance

Every open merge request has one report file, `review/MR-<id>.md`, committed **on that MR's source branch**, not on `develop`.

```
Round 1  → report written and pushed to the source branch
         → developer applies fixes and pushes
Round 2  → the same file is updated item by item
         → … until every item is closed
         → the file is deleted and pushed
         → merge happens through GitLab, by a human
```

## Five rules that must not be broken

1. **Reports are written in English.** The `MR-<id>.md` files are entirely English; only the process document (`review/README.md`) is in Persian.
2. **The user's working tree is never disturbed.** Use `git worktree` to check out the source branch, never `git checkout` in the main folder.
3. **Record the reviewed commit in the report header.** Without it the next round cannot take an incremental diff.
4. **`✅ fixed` only after reading the code.** A commit message is not evidence.
5. **Merging into `develop` is not the reviewer's job.** Once the report is closed out, just report readiness.

## Diff scope

Always against the merge-base:

```powershell
$base = git merge-base origin/develop mr-<id>
git diff $base..mr-<id>
```

## Finding open MRs without the GitLab API

GitLab exposes `refs/merge-requests/<id>/head`, so they can be listed without a token:

```powershell
git ls-remote origin "refs/merge-requests/*/head"
```

Any MR whose head is not an ancestor of `develop` has not been merged. This does not prove it is *open* — it may be closed or a draft. For real status, titles and comments, `glab` is required:

```powershell
glab auth login --hostname ci.tamin.ir   # once, by the user
```

Related: [[Adding-a-Feature]] · [[Naming-Conventions]] · [[CI-CD]]
