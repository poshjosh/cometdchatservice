#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
 */
pipeline {
    agent any
    environment {
        ARTIFACTID = readMavenPom().getArtifactId();
        VERSION = readMavenPom().getVersion()
        PROJECT_NAME = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "poshjosh/${PROJECT_NAME}";
        IMAGE_NAME = IMAGE_REF.toLowerCase()
    }
    options {
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '4'))
        skipStagesAfterUnstable()
        disableConcurrentBuilds()
    }
    triggers {
// @TODO use webhooks from GitHub
// Once in every 2 hours slot between 0900 and 1600 every Monday - Friday 
        pollSCM('H H(8-16)/2 * * 1-5')
    }
    stages {
        stage('All') {
            agent {
                dockerfile {
                    filename 'Dockerfile'
                    registryCredentialsId 'dockerhub-creds' // Must have been specified in Jenkins
                    args '-v /usr/bin/docker:/usr/bin/docker -v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -v "$PWD":/usr/src/app -v "$HOME/.m2":/root/.m2 -v "$PWD/target:/usr/src/app/target" -w /usr/src/app'
                    additionalBuildArgs "-t ${IMAGE_NAME}"
                }
            }
            stages{
                stage('Clean & Build') {
                    steps {
                        sh 'mvn -B clean compile'
                    }
                }
                stage('Unit Tests') {
                    steps {
                        sh 'mvn -B resources:testResources compiler:testCompile surefire:test'
                    }
                    post {
                        always {
                            junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
                        }
                    }
                }
                stage('Quality Analysis') {
                    parallel {
                        stage ('Integration Tests') {
                            steps {
                                sh 'mvn -B failsafe:integration-test failsafe:verify'
                            }
                        }
                        stage('Sanity Check') {
                            steps {
                                sh 'mvn -B checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
                            }
                        }
                        stage('Sonar Scan') {
                            environment {
                                SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                            }
                            steps {
                                sh "mvn -B sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW"
                            }
                        }
                    }
                }
                stage('Documentation') {
                    steps {
                        sh 'mvn -B site'
                    }
                    post {
                        always {
                            publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
                        }
                    }
                }
//                stage('Package') {
//                    steps {
//                        sh 'mvn -B jar:jar'
//                    }
//                }
                stage('Install Local') {
                    steps {
                        sh 'mvn -B jar:jar source:jar install:install'
                    }
                }
//                stage('Deploy Image') {
//                    steps {
//                        withDockerRegistry([url: '', credentialsId: 'dockerhub-creds']) {
//                            sh ''' 
//                                "docker push $IMAGE_NAME"
//                                "docker rmi $IMAGE_NAME"
//                            '''                            
//                        }
//                    }
//                }
            }
        }
    }
    post {
        always {
            deleteDir() /* clean up workspace */
        }
        failure {
            mail(
                to: 'posh.bc@gmail.com', 
                subject: "$IMAGE_NAME - Build # $BUILD_NUMBER - FAILED!", 
                body: "$IMAGE_NAME - Build # $BUILD_NUMBER - FAILED:\n\nCheck console output at ${env.BUILD_URL} to view the results."
            )
        }
    }
}
