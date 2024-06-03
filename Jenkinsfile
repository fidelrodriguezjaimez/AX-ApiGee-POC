pipeline {
  agent any

  stages {

    stage('Git CheckOut') {
      steps {
        git(url: 'https://github.com/fidelrodriguezjaimez/AX-ApiGee-POC.git', branch: 'feature/Apigee-poc')
        echo 'CheckOut realizado con exito'
      }
    }

    stage('Replace Tokens in POM') {
      steps {
        script {
            // Leer el archivo del repositorio
            def configFile = "${ROOT_DIRECTORY}/${POM_FILE}"
            def fileContent = readFile configFile

                // Reemplazar los tokens
                configFile = fileContent.replace('#{apiName}#', API_NAME)
          configFile = fileContent.replace('${deployment.suffix}', SUFFIX)

                // Guardar el archivo modificado en el mismo lugar
                writeFile file: configFile, text: fileContent

                // Mostrar el archivo modificado (opcional)
                echo readFile(configFile)
              }
          }
    }

     stage('Maven clean install') {
        steps {
            script {
                // Ejecuta el comando Maven
                sh "mvn -f src/main/apigee/apiproxies/${PROXY_PATH}/pom.xml clean install -Pbuildapi"
            }
        }
    }
        
  }
  environment {
    SONAR_KEY = '23_Coppel_TestJenkinsGitHub'
    SONAR_SERVER = 'https://devtools.axity.com/sonarlts'
    HARBOR_URL = 'demo.goharbor.io'
    HARBOR_USERNAME = 'fidel.rodriguez'
    PROXY_PATH = 'swsysproxy'
    ROOT_DIRECTORY = "src/main/apigee/apiproxies/${PROXY_PATH}"
    API_NAME = 'APIGEE-POC'
    POM_FILE = 'pom.xml'
    SUFFIX = '-V1'
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
