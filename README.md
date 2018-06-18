# Jenkins Habitat Executor Plugin

This plugin allows for the abstraction of building, uploading, and promoting
habitat packages.

## Pre-Reqs

- Must have Jenkins setup for building habitat packages  
  - A designated origin and the corresponding keys  
- Habitat Auth Token specified as a Jenkins credential  

## Usage

The main usage would be as follows to build, upload, and promote the package:

```
pipeline {
    agent any

    environment {
        HAB_BLDR_URL = 'https://bldr.habitat.sh'
    }

    stages {
        stage('scm') {
            steps {
                git url: 'https://github.com/skylerto/nexus-habitat.git', branch: 'master'
            }
        }
        stage('build') {
            steps {
                habitat task: 'build', directory: "."
            }
        }
        stage('upload') {
            steps {
              withCredentials([string(credentialsId: 'depot-token', variable: 'HAB_AUTH_TOKEN')]) {
                  habitat task: 'upload', directory: "${workspace}"
              }
            }
        }
        stage('promote') {
          steps {
            withCredentials([string(credentialsId: 'depot-token', variable: 'HAB_AUTH_TOKEN')]) {
                    habitat task: 'promote', channel: 'stable', directory: "${workspace}"
                }
            }
        }
    }
}
```

The directory for the `build` task must be local to the repository as the studio is created under the root of the repository.
