pipeline {
  agent any

  stages {

    stage('Git CheckOut') {
      agent any
      steps {
        git(url: 'https://github.com/fidelrodriguezjaimez/AX-ApiGee-POC.git', branch: 'feature/Apigee-poc')
        echo 'CheckOut realizado con exito'
      }
    }

     stage('Maven clean install') {
        steps {
            script {
                // Ajusta la variable proxyPath si es necesario
                def proxyPath = env.proxyPath ?: 'swsysproxy'
                
                // Ejecuta el comando Maven
                sh "mvn -f src/main/apigee/apiproxies/${proxyPath}/pom.xml clean install -Pbuildapi"
            }
        }
    }
        
  }
  environment {
    SONAR_KEY = '23_Coppel_TestJenkinsGitHub'
    SONAR_SERVER = 'https://devtools.axity.com/sonarlts'
    HARBOR_URL = 'demo.goharbor.io'
    HARBOR_USERNAME = 'fidel.rodriguez'
    proxyPath = 'swsysproxy'
  }
  post {
    success {
      echo 'Esto se ejecutará solo si se ejecuta correctamente'
    }

    failure {
      echo 'Esto se ejecutará solo si falla'
    }
  }
}
