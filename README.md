# GHS (Git Health Service)

Git Reposiory optimization is key for maintaining high standard performance in particular when working with monorepos.

Performance degradation in Git repositories over time can occur due to a combination of factors. Git is a distributed version control system that is designed to handle a wide range of projects and workflows efficiently, but as repositories grow and usage patterns evolve, certain issues might lead to performance problems. Some common reasons for performance degradation in Git repositories:

* **Repository Size**: As your repository accumulates more and more commits, branches, tags, and files, its size increases. This can slow down operations like cloning, fetching, and pushing, as larger amounts of data need to be transferred and processed.

* **Number of Commits**: Git stores the entire history of a repository, and as the number of commits grows, some operations that involve traversing the commit graph or computing diffs between commits can become slower.

* ***Inefficient Workflow**: Certain Git operations might be slow if your workflow involves a high frequency of force-pushes, rebasing, or complex merge scenarios.

GHS aims to provide an open source solution to monitor, create intelligent decision rules and optimize your Git repositories.

The tool provides 3 main capabilities:

* **metric collection:** allows collection of repository metrics to monitor the health of the Git repository. For example: number of loose objects, number of packfiles, number of directories, etc. Metrics will be used by the *intelligent scheduler* to decide when to execute a maintenance task.
* **intelligent task scheduling:** this is the brain of the system. A decision AI based rule engine which decides which maintenace task need to be scheduled.
* **task execution:** depending on historical task execution, status of the system, etc, define the parameters to run a task with. Tasks can be removal of leftover lock files, keep files, GCs, repacks.

GHS is founded on two key principles:
* **agnosticity of the Git server:** no matters what Git server is running, Gerrit, Github, Gitlab, the application will work. It is agnostic of the Git server implementatiom.
* **modularity:** it is possible to mix and match the functionality offered by the application. For example, if you already have in place a system to collect metrics, you could use it to feed the *intelligent scheduler*.