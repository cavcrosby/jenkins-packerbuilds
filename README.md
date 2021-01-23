# jenkins-packerbuilds

This repo serves the main repo for a Jenkins job/project called 'packerbuilds'. 'packerbuilds' is a Jenkins job/project attempting to regularly build VM images for me to use as a testing ground. At least, this is the only current intention of this project.

The actual building of VMs is done using **HashiCorp**'s [packer](https://github.com/hashicorp/packer).

This repo will constantly change, hence if anyone has an interest in creating the same job on a Jenkins instance, I will provide the rough steps needed todo this below but I'd advised making a fork of this repo.

# Repos Used In Conjunction

Same recommendation as above, if you intend to use the job as currently implemented or want to use these repos, I'd suggest forking these as well.

- https://github.com/reap2sow1/os-build-confs
  - Holds kickstart/preseeds, these files allow fully automatic installations for Unix-like operating systems.
  - https://en.wikipedia.org/wiki/Kickstart_(Linux)
  - https://en.wikipedia.org/wiki/Preseed
- https://github.com/reap2sow1/packer-build-templates
  - Holds packer build templates.
  - Contains a script(s) used to evaluate placeholders in these templates.
  - https://www.packer.io/docs/templates
- https://github.com/reap2sow1/packer-build-shell-provisioners
  - Holds shell scripts that packer uses as a provisioner.
  - https://www.packer.io/docs/provisioners/shell

## Installation

This installation guide assumes that a normal Jenkins instance has already been setup on a server and that the user has some familiarity with using Jenkins. On-top of the normal setup, the following plugins will also need to be installed:

- [Active Choices Plug-in](https://plugins.jenkins.io/uno-choice/) 
- [Job DSL](https://plugins.jenkins.io/job-dsl/)
- [Parameterized-Scheduler](https://plugins.jenkins.io/parameterized-scheduler/)

You may need to swap out the jenkins-packerbuilds url if you choose todo a fork. Further below I will note where Jenkins configuration differences may be worth looking into and reconfiguring.

On the server that is running jenkins, in a shell, run the following:
```shell
git clone https://github.com/reap2sow1/jenkins-packerbuilds
cd jenkins-packerbuilds
chmod 755 prepare-seedjob
./prepare-seedjob
```
From there, in your Jenkins instance, proceed to create a new seed job. The only job configuration needed for this new seed job is a new build step. When adding the new build step, click on the option _**Process Job DSLs**_.

![image](https://user-images.githubusercontent.com/31086993/105564206-2acdc680-5cef-11eb-9523-26f1f645cc0a.png)

Then select the radio button named, "Use the provided DSL script", and paste in the entire contents of packerbuilds-job-dsl.groovy from the jenkins-packerbuilds repo. Finish by saving this configuration and running a build on the seed/phony job. This should generate the newly 'packerbuilds' job/project.

![image](https://user-images.githubusercontent.com/31086993/105564218-33be9800-5cef-11eb-9e6a-fadb8266e714.png)

From there, there will be some additional setting up (again, assuming you intend on using this current implementation):
- Installing **HashiCorp**'s packer, and configuring the sole build step to have the packer executable's path (PACKER_EXE).
  - the sole build step at the moment might have missing values needed in some of its env variables.
- Installing Oracle's VirtualBox.
- Jenkins 'Credentials' will need to be setup, though they may not get used by a build as it was just when this repo was private. These "Credentials" need to be used in the 'Bindings' section.
- An SMTP server to connect to will also need to be setup, as the job does mail a recipient(s) incase a build fails.


## Installation Notes

- prepare-seedjob assumes that the JENKINS_HOME is set to /var/lib/jenkins/, if this is not the case, then you will want to reconfigure the prepare-seedjob script with the appropriate JENKINS_HOME.

- Using forked repos will require changes to the main 'packerbuilds' script in the scripts folder. Look for the vars: OS_BUILD_CONFS_REPO_URL, SHELL_PROVISIONERS_REPO_URL, and PACKER_BUILD_TEMPLATES_REPO_URL.

