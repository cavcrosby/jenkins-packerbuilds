freeStyleJob ('packerbuilds') {
    
    /*
    #   For Job-DSL API reference see: https://jenkinsci.github.io/job-dsl-plugin/
    #
    #   NOTE: Be sure to run the 'prepare-seedjob' script first, or else this seed will fail
    #   to create the packerbuilds project!
    #     
    #   The following bindings will need to be added manually to packerbuilds job/project configuration.
    #   Username Variable/Password Variable Respectfully:
    #   JENKINS_GIT_CREDENTIAL_USERNAME 
    #   JENKINS_GIT_CREDENTIAL_PASSWORD
    */

    // Allows Jenkins to schedule and execute multiple builds concurrently.
    // NOTE: To add, I've seen odd behavior doing concurrent packerbuilds builds, 
    // so for now it's best todo them one at a time.
    concurrentBuild(false) 

    // Allows to parameterize the job. 
    parameters {
        // Defines a parameter that dynamically generates a list of value options for a build parameter using a Groovy script or a script from the Scriptler catalog. 
        activeChoiceParam('OPERATING_SYSTEM') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./jobconfs/operating_systems_parameter.groovy'))
                fallbackScript('')
            }
        }

        /* 
            Defines a parameter that dynamically generates a list of value options for a build parameter using a Groovy
            script or a script from the Scriptler catalog and that dynamically updates when the value of other job jobconfs
            controls change. 
        */
        activeChoiceReactiveParam('OPERATING_SYSTEM_VERSION') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./jobconfs/operating_system_versions_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('OPERATING_SYSTEM')
        }

        activeChoiceReactiveParam('ISO_FILE') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./jobconfs/iso_files_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('OPERATING_SYSTEM_VERSION')
        }

        activeChoiceReactiveParam('PACKER_BUILDER') {
            description('')
            filterable(false)
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(readFileFromWorkspace('./jobconfs/packer_builder_parameter.groovy'))
                fallbackScript('')
            }
            referencedParameter('ISO_FILE')
        }

        // Defines a simple text parameter, where users can enter a string value. 
        stringParam('OTHER_PACKERBUILD_NAME_CRITERIA', '', 'The input will be appended to the build name with a dash, (e.g. ${buildname}-${OTHER_PACKERBUILD_NAME_CRITERIA}). Useful for making a special packerbuild.')
        stringParam('RECORD_BUILDENV_VARS', '', '''Currently this should be a space separated character string (e.g. "FOO BAR FOOBAR") used to record env var names and values into the 'packerbuild.conf' file.''')
        stringParam('DRY_RUN', '', '''Same as performing a normal build except the packer executable does not actually run. Useful to do a manually build on the generated template from the build directory. Any input for this field will cause  'dry run'.''')
        stringParam('OS_BUILD_CONF_NAME', '', '')
        stringParam('SHELL_PROVISIONER_NAME', '', '')
        stringParam('PACKER_BUILD_TEMPLATE_NAME', '', '')
        stringParam('PACKER_BUILD_EVALUSERVARS_NAME', '', '')
    }

    // Allows a job to check out sources from an SCM provider. 
    scm {
        git {
            remote {
                url('https://github.com/reap2sow1/jenkins-packerbuilds')
            }
            branch('main')
        }
    }

    // Adds build triggers to the job.
    triggers {
        // Adds support for passing parameters to parameterized builds on top of the default scheduler. 
        parameterizedCron {

            // follow convention of cron, schedule with name=value pairs at the end of each line. 
            parameterizedSpecification(readFileFromWorkspace('./jobconfs/parameterizedcrons'))

        }
    }

    steps {
        // Runs a shell script. 
        shell(readFileFromWorkspace('./jobconfs/buildstep'))
    }

    // Adds post-build actions to the job.
    publishers {
        // Archives artifacts with each build. 
        archiveArtifacts('*/output/*')
    }

}
