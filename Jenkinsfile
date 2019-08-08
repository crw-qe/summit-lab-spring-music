pipeline {
	agent {
		label 'maven'
	}

	environment {
		BUILD_VERSION = "1.0.0.${currentBuild.number}"
	}

	stages {
		stage('Build App') {
			steps {
				sh "mvn versions:set clean package -DnewVersion=${env.BUILD_VERSION} -DskipTests"
			}
		}

		stage('Unit Test') {
		  steps {
		  	sh "mvn versions:set verify -DnewVersion=${env.BUILD_VERSION}"
		  }
		}

		stage('Publish Artifact') {
			steps {
				sh "mvn versions:set deploy -DskipTests -Dmaven.install.skip=true -DnewVersion=${env.BUILD_VERSION} -DaltDeploymentRepository=libs-snapshot::default::http://nexus.labs-infra.svc/nexus/content/repositories/libs-snapshot -s misc/config/settings.xml"
			}
		}

		stage('Build Image') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							openshift.startBuild("spring-music", "--from-file=target/summit-lab-spring-music-${env.BUILD_VERSION}.jar").logs("-f")
						}
					}
				}
			}
		}

		stage('Deploy') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							dc = openshift.selector("dc", "spring-music")
							dc.rollout().latest()
							timeout(10) {
								dc.rollout().status()
							}
						}
					}
				}
			}
		}

		stage('Integration Test') {
			steps {
				script {
					sh "curl -s http://spring-music:8080/actuator/health | grep 'UP'"
				}
			}
		}

		stage('Promote to Prod') {
			steps {
				timeout(time:15, unit:'MINUTES') {
					input message: "Approve Promotion to Prod?", ok: "Promote"
				}

				script {
					openshift.withCluster() {
						openshift.tag("dev/spring-music:latest", "prod/spring-music:prod")
					}
				}
			}
		}

		stage('Deploy to Prod') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject('prod') {
							if (!openshift.selector('dc', 'spring-music').exists()) {
								openshift.newApp("spring-music:prod").narrow("svc").expose()
								// openshift.selector('dc', 'spring-music').delete()
								// openshift.selector('svc', 'spring-music').delete()
								//openshift.selector('route', 'spring-music').delete()
							}
							// else {
								// openshift.newApp("spring-music:prod").narrow("svc").expose()
							// }
						}
					}
				}
			}
		}
	}
}
