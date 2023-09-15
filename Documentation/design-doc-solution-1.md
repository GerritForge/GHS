# Solution 1 - Single application with multiple responsabilities

## Overview

The application will work independently from the Git server implementation.

The responsibility of the application will be:

### Metrics collection

* Collection will be time based
* Frequency will be defined in a configuration file per repository
* Metrics will be stored in memory
* Metrics will be exposed in an API endpoint

### Task execution

* Task scheduling strategy will be defined in a configuration file per repository and can be:
  * Event based: simple or complex
  * Time based: cron-like pattern
* Manual task scheduling will be triggered via an API endpoint
* The status of the task will be exposed via an API endpoint
* The historical results will be stored in a log
* Enforce only one task at a time will run per project

### Task definition

* Task definition will be provided by a configuration file per repo, including:
  * task command (`git`, `rm`, `jgit`, ...)
  * task parameters (`prunePackExpiry`, `-fr`)

## Pros and Cons

## Pros

* Simple communication among components
* No external components to manage
* Full audit of logs

## Cons

* Volatile state: if the application is restarted the state is lost
* Time based collection of metrics is not efficient and accurate
* No correlation between events happening on different nodes