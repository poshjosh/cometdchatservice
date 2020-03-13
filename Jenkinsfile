#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
 * @see https://github.com/carlossg/docker-maven
 */
pipeline {
    agent any
    environment {
        APP_PORT = '8092'
        ARTIFACTID = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        APP_ID = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "poshjosh/${APP_ID}"
        IMAGE_NAME = IMAGE_REF.toLowerCase()
        RUN_ARGS = "-v /home/.m2:${HOME}/.m2 -v ${PWD}:/usr/src/app -v /home/.m2:/root/.m2 -v ${PWD}/target:/usr/src/app/target -w /usr/src/app -p ${APP_PORT}:${APP_PORT}"
    }
    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
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
        stage('Build Image') {
            steps {
                script {
                    def additionalBuildArgs = "--pull"
                    if (env.BRANCH_NAME == "master") {
                        additionalBuildArgs = "--pull --no-cache"
                    }
                    docker.build("${IMAGE_NAME}", "${additionalBuildArgs} .")
                }
            }
        }
        stage('Build Artifact') {
            steps {
                script{
                    ws('/usr/src/app') {
                        echo "HOME = ${HOME}"
                        sh 'printenv'
                        docker.image("${IMAGE_NAME}").inside("${RUN_ARGS}"){
                            echo "HOME = ${HOME}"
                            sh 'printenv'    
                            sh 'cat /usr/share/maven/ref/settings-docker.xml'
                            sh 'mvn -X -B clean compiler:compile'
                        }
                    }
                }
            }
        }
        stage('Unit Tests') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B resources:testResources compiler:testCompile surefire:test'
                    }
                }
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        testResults: '**/target/surefire-reports/TEST-*.xml'
                    )
                }
            }
        }
        stage('Quality Analysis') {
            parallel {
                stage('Integration Tests') {
                    steps {
                        script{
                            docker.image("${IMAGE_NAME}").inside{
                                sh 'mvn -B failsafe:integration-test failsafe:verify'
                            }
                        }
                    }
                }
                stage('Sanity Check') {
                    steps {
                        script{
                            docker.image("${IMAGE_NAME}").inside{
                                sh 'mvn -B checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
                            }
                        }
                    }
                }
                stage('Sonar Scan') {
                    environment {
                        SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                    }
                    steps {
                        script{
                            // -Dsonar.host.url=${env.SONARQUBE_HOST}
                            docker.image("${IMAGE_NAME}").inside{
                                sh "mvn -B sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW"
                            }
                        }
                    }
                }
            }
        }
        stage('Documentation') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B site:site'
                    }
                }
            }
            post {
                always {
                    publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
                }
            }
        }
        stage('Run Image') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").run()
                }
            }
        }
        stage('Install Local') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B jar:jar source:jar install:install'
                    }
                }
            }
        }
        stage('Deploy Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('', 'dockerhub-creds') { // Must have been specified in Jenkins
                        sh "docker push ${IMAGE_NAME}"
                    }
                }
            }
        }
    }
    post {
        always {
            deleteDir() /* clean up workspace */
            sh "docker system prune -f --volumes"
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
