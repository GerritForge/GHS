# Summary

Git Reposiory optimization is key for maintaining high standard performance in particular when working with monorepos. Note we are not talking about Gerrit managed repository, but Git repository independently from the server.

# Background

Performance degradation in Git repositories over time can occur due to a combination of factors. Git is a distributed version control system that is designed to handle a wide range of projects and workflows efficiently, but as repositories grow and usage patterns evolve, certain issues might lead to performance problems. Some common reasons for performance degradation in Git repositories:

* Repository Size: As your repository accumulates more and more commits, branches, tags, and files, its size increases. This can slow down operations like cloning, fetching, and pushing, as larger amounts of data need to be transferred and processed.

* Number of Commits: Git stores the entire history of a repository, and as the number of commits grows, some operations that involve traversing the commit graph or computing diffs between commits can become slower.

* Large Files: Binary files or other large assets added to the repository can contribute significantly to its size. Git wasn't designed to handle large binary files efficiently, and operations involving these files can be slow.

* Inefficient Workflow: Certain Git operations might be slow if your workflow involves a high frequency of force-pushes, rebasing, or complex merge scenarios.

To improve the performance of a Git repository, you can take several proactive measures that address various aspects of repository management. Here's a list of actions you can take to enhance Git repository performance:

* Regular Maintenance:
   - Run `git gc`, `git repack` and `git prune` periodically to optimize the repository's internal storage and remove unnecessary objects

* Avoid storing large files in Git:
   - Use Git LFS (Large File Storage) to manage large binary files separately from the repository.

Improving Git repository performance is an ongoing effort that requires a combination of technical solutions, process adjustments, and a commitment to maintaining a clean and efficient repository structure.

## Available OpenSource solution/workflow

Currently the OpenSource community provides the following tools:

### Metrics exposure

* one-off collection: [git-sizer](https://github.com/github/git-sizer), [git-stats](https://github.com/hoxu/gitstats), [git-count-objects](https://git-scm.com/docs/git-count-objects)
* systematic collection: Gerrit [git-repo-metrics](https://gerrit.googlesource.com/plugins/git-repo-metrics/)

### Repository maintenance

The orchestration of optimization activities on different repositories is done mainly via complex [bash scripts](https://gerrit.googlesource.com/aws-gerrit/+/refs/heads/master/maintenance/git-gc/scripts/) scheduled periodically.

For example, these scripts provide the following functionalities:
* ensure a single GC process is running
* check for stale GC locks or keep packfiles
* calculate operational parameters (for example: Memory allocated for the process, `gc.pruneExpire`)
* produce metrics before and after a run

## Issues

* The whole workflow is difficult to maintain and troubleshoot
* The workflow is not agnostic from the git server implementation
* A general lack of automation makes the whole workflow error prone
* The set of operations covered by the tools is incomplete
* Most of the decision are based on personal experience

# Primary use cases

* As a Git server admin...
    I want to be able to consult metrics without manual intervention on the system
    I want to be able to schedule a maintenance tasks on repositories seamlessly
    I want to be able to define the intelligence of automation rules

# Secondary use cases

* As a Git server admin...
    I want to see a history of the automation tasks
    I want to see the status of the automation tasks

# Acceptance Criteria

* The admin is the only one responsible to configure the intelligence of the automation rules
* Tasks can be triggered by events, time, intelligent rules or manually by the admin
* The system has to be decoupled from any specific Git server implementation
* The system should not affect the normal performance of the Git server by more than 15%
* The system will allow an existing Git server to maintain its throughput over time