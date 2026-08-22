pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out Expense Tracker project from GitHub...'
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling Java source code...'

                bat '''
                    if exist out rmdir /s /q out
                    mkdir out
                    javac -d out ExpenseServer.java
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Checking compiled Java classes...'

                bat '''
                    if not exist out\\ExpenseServer.class (
                        echo ExpenseServer.class not found!
                        exit /b 1
                    )

                    echo Java compilation test passed successfully.
                '''
            }
        }

        stage('Package') {
    steps {
        echo 'Packaging Expense Tracker application...'

        bat '''
            if exist package rmdir /s /q package

            mkdir package
            mkdir package\\out
            mkdir package\\frontend

            xcopy /E /I /Y out package\\out

            copy /Y index.html package\\frontend\\index.html
            copy /Y style.css package\\frontend\\style.css
            copy /Y script.js package\\frontend\\script.js

            echo Application packaged successfully.
        '''
    }
}

        stage('Archive') {
            steps {
                echo 'Archiving Expense Tracker application...'

                archiveArtifacts artifacts: 'package/**',
                fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Expense Tracker application...'

                bat '''
                    if exist "C:\\ProgramData\\Jenkins\\.jenkins\\userContent\\expense-tracker" rmdir /s /q "C:\\ProgramData\\Jenkins\\.jenkins\\userContent\\expense-tracker"

                    mkdir "C:\\ProgramData\\Jenkins\\.jenkins\\userContent\\expense-tracker"

                    xcopy /E /I /Y package\\frontend "C:\\ProgramData\\Jenkins\\.jenkins\\userContent\\expense-tracker"

                    echo Expense Tracker deployed successfully!
                '''
            }
        }
    }

    post {

        success {
            echo '======================================'
            echo '       Expense Tracker'
            echo '       CI/CD PIPELINE SUCCESSFUL'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo '       Expense Tracker'
            echo '       CI/CD PIPELINE FAILED'
            echo '======================================'
        }
    }
}
