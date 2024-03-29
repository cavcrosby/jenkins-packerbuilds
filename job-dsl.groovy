freeStyleJob ('packerbuilds') {
    
    /*
    #   For Job-DSL API reference see: https://jenkinsci.github.io/job-dsl-plugin/
    #  
    #   The following bindings will need to be added manually to packerbuilds
    #   job/project configuration.
    #   Username variable/password variable respectfully:
    #   JENKINS_GITHUB_CREDENTIAL_USERNAME
    #   JENKINS_GITHUB_CREDENTIAL_SECRET
    #
    */

    /* 
        Allows Jenkins to schedule and execute multiple builds concurrently. To add,
        I've seen odd behavior doing concurrent packerbuilds builds, so for now it's
        best todo them one at a time.
    */
    concurrentBuild(false) 

    logRotator {
        numToKeep(10)
        artifactNumToKeep(10)
    }

    // Allows to parameterize the job. 
    parameters {
        /* 
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
        */
        activeChoiceParam('BUILD_TYPE') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/build_type_parameter.groovy'))
                fallbackScript('')
            }
        }

        activeChoiceReactiveParam('PROJECT') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/project_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('BUILD_TYPE')
        }

        /* 
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
            The choices dynamically update when the value of BUILD_TYPE or PROJECT parameter is picked.
        */
        activeChoiceReactiveParam('OPERATING_SYSTEM') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/operating_systems_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('BUILD_TYPE')
            referencedParameter('PROJECT')
        }

        /* 
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
            The choices dynamically update when the value of OPERATING_SYSTEM parameter is picked.
        */
        activeChoiceReactiveParam('OPERATING_SYSTEM_VERSION') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/operating_system_versions_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('OPERATING_SYSTEM')
        }

        activeChoiceReactiveParam('GUEST_OS_TYPE') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/guest_os_type_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('OPERATING_SYSTEM')
        }

        /* 
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
            The choices dynamically update when the value of OPERATING_SYSTEM_VERSION or
            OPERATING_SYSTEM parameter is picked.
        */
        activeChoiceReactiveParam('ISO_FILE') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/iso_files_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('OPERATING_SYSTEM_VERSION')
            referencedParameter('OPERATING_SYSTEM')
        }

        /* 
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
            The choices dynamically update when the value of ISO_FILE or OPERATING_SYSTEM
            parameter is picked.
        */
        activeChoiceReactiveParam('PACKER_BUILDER') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/packer_builder_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('ISO_FILE')
            referencedParameter('OPERATING_SYSTEM')
        }

        /*
            Defines a parameter that dynamically generates a list of value options for a
            build parameter using a Groovy script or a script from the Scriptler catalog.
            The default value dynamically updates when the value of PROJECT is picked.
        */
        activeChoiceReactiveReferenceParam('SHELL_PREPROCESSOR') {
            description('The name of a shell script located in the respective repo to source (e.g. not running, sourced into the same shell session) before running packer.')
            omitValueField(true)
            choiceType('FORMATTED_HTML')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/shell_preprocessor_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('PROJECT')
        }

        activeChoiceReactiveReferenceParam('OS_BUILD_CONF') {
            description('')
            omitValueField(true)
            choiceType('FORMATTED_HTML')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/os_build_conf_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('PROJECT')
        }

        activeChoiceReactiveReferenceParam('SHELL_PROVISIONER') {
            description('')
            omitValueField(true)
            choiceType('FORMATTED_HTML')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/shell_provisioner_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('PROJECT')
        }

        activeChoiceReactiveReferenceParam('PACKER_TEMPLATE') {
            description('')
            omitValueField(true)
            choiceType('FORMATTED_HTML')
            groovyScript {
                script(readFileFromWorkspace('./configs/job/parameters/packer_template_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('PROJECT')
        }

        // Defines a simple text parameter, where users can enter a string value.
        stringParam('OTHER_BUILD_NAME_CRITERIA', '', 'The input will be appended to the build name with a dash, (e.g. _buildname-_OTHER_BUILD_NAME_CRITERIA). Useful for making a special packerbuilds.')
        stringParam('RECORD_BUILDENV_VARS', '', '''Currently this should be a space separated character string (e.g. "FOO BAR FOOBAR") used to record env var names and values into the 'packerbuilds.conf' file.''')
        stringParam('DRY_RUN', '', '''Same as performing a normal build except the packer executable does not actually run. Useful to do a manually build on the generated template from the build directory. Any input for this field will cause  'dry run'.''')
    }

    // Allows a job to check out sources from an SCM provider. 
    scm {
        git {
            remote {
                /* 
                    A shorten tenary operator...the elvis operator!
                    https://groovy-lang.org/operators.html#_elvis_operator
                    Aside, this is also a credential job binding but these should not conflict.
                */
                credentials(System.getenv('JENKINS_GITHUB_CREDENTIAL_ID') ?: '')
                url('https://github.com/cavcrosby/jenkins-packerbuilds')
            }
            branch('main')
        }
    }

    // Adds build triggers to the job.
    triggers {
        /*  
            Adds support for passing parameters to parameterized builds on top of the
            default scheduler.
        */
        parameterizedCron {

            /* 
                Follow convention of cron, schedule with name=value pairs at the end of each
                line. 
            */
            parameterizedSpecification(readFileFromWorkspace('./configs/job/parameterizedcrons'))

        }
    }

    // Adds pre/post actions to the job. 
    wrappers {
        // Binds environment variables to credentials.
        credentialsBinding {
            usernamePassword {
                /* 
                    Name of an environment variable to be set to the username during the build.
                */
                usernameVariable("JENKINS_GITHUB_CREDENTIAL_USERNAME ")
                /* 
                    Name of an environment variable to be set to the password during the build.
                */
                passwordVariable("JENKINS_GITHUB_CREDENTIAL_SECRET")
                // Credentials of an appropriate type to be set to the variable.
                credentialsId(System.getenv('JENKINS_GITHUB_CREDENTIAL_ID') ?: '')
            }
        }

        /* 
            Defines an absolute timeout with a maximum  build time of one hour and thirty
            minutes.
        */
        timeout {
            absolute(90)
        }
    }

    steps {
        // Runs a shell script. 
        shell(readFileFromWorkspace('./configs/job/build'))
    }

    // Adds post-build actions to the job.
    publishers {
        // Archives artifacts with each build. 
        archiveArtifacts('*/output/*')

        /* 
            If configured, Jenkins will send out an e-mail to the specified recipients
            when a certain important event occurs. 
        */
        mailer {
            recipients('conner@cavcrosby.tech')
            notifyEveryUnstableBuild(true)
            /* 
                If this option is checked, the notification e-mail will be sent to individuals
                who have committed changes for the broken build (by assuming that those
                changes broke the build).
            */
            sendToIndividuals(false) 
        }
    }

}
