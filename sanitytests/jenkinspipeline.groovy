def path = "/home/bhanu"
pipeline {
    agent any

    parameters {
        string(name: 'jobname', description: 'Enter the job name')
        string(name: 'metricstype', description: 'Enter the metrics name ex:systemMetrics')
        string(name: 'nodename', description: 'Enter the node name')
        string(name: 'changestatus', description: 'Enter the state of the node to which it needs to be changed')
    }

    stages {
        stage('Create and Run a job to test Buildevent') {
            steps {
                script {
                    sh """
                        cd ${path}
                        echo ${jobname} | python3 createjob.py
                    """
                }
            }
        }
        stage('Check the response in jenkins fine log for BuildEvent') {
            steps {
                script {
                    sh """
                        cd ${path}
                        python3 checkfinelogsbuildevent.py
                    """
                }
            }
        }
        stage('Check the response in jenkins fine log for Metrics') {
            steps {
                script {
                    sh """
                        cd ${path}
                        echo ${metricstype} | python3 checkfinelogsmetrics.py
                    """
                }
            }
        }
        stage('Change the status of the Node') {
            steps {
                script {
                    sh """
                        cd ${path}
                        python3 changenodestatus.py ${nodename} ${changestatus}
                    """
                }
            }
        }
        stage('Check the response in jenkins fine log for SystemEvent') {
            steps {
                script {
                    sh """
                        cd ${path}
                        python3 checkfinelogsSystemEvents.py
                    """
                }
            }
        }
        stage('Print the output file') {
            steps {
                script {
                    sh """
                        cd ${path}
                        python3 printoutput.py
                    """
                }
            }
        }
    }
}
