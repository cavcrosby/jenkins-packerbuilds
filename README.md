# GUIDE IS OUT OF DATE

# jenkins-packerbuilds

This repo serves as the main repo for a Jenkins job/project called 'packerbuilds'. 'packerbuilds' is a freestyle Jenkins job/project attempting to regularly build VM images for me to use as a testing ground. At least, this is the current intention of this project.

The actual building of VMs is done using **HashiCorp**'s [packer](https://github.com/hashicorp/packer).

This repo will constantly change, hence if anyone has an interest in creating the same job on a Jenkins instance, I will provide the rough steps needed todo this below but I'd advised making a fork of this repo.

# Repos Used In Conjunction

Same recommendation as above, if you intend to use the job as currently implemented or want to use these repos, I'd suggest forking these as well.

- https://github.com/cavcrosby/os-init-confs
  - Holds kickstart/preseeds, these files allow fully automatic installations for some Linux operating systems.
  - https://en.wikipedia.org/wiki/Kickstart_(Linux)
  - https://en.wikipedia.org/wiki/Preseed
- https://github.com/cavcrosby/packer-templates
  - Holds packer build templates.
  - Contains a script(s) used to evaluate placeholders in these templates.
  - https://www.packer.io/docs/templates
- https://github.com/cavcrosby/packer-shell-provisioners
  - Holds shell scripts that packer uses as a provisioner.
  - https://www.packer.io/docs/provisioners/shell

## Installation

This installation guide assumes that a normal Jenkins instance has already been setup on a server and that the user has some familiarity with using Jenkins. On-top of the normal setup, the following plugins will also need to be installed:

- [Active Choices Plug-in](https://plugins.jenkins.io/uno-choice/) 
- [Job DSL](https://plugins.jenkins.io/job-dsl/)
- [Parameterized-Scheduler](https://plugins.jenkins.io/parameterized-scheduler/)

Further below I will note where Jenkins configuration differences may be worth looking into and reconfiguring.

In your Jenkins instance, proceed to create a new seed job (note, this is just a normal Jenkins job but its purpose to just to create/generate another job). Two job configurations are needed for this new seed job. First, under 'Source Code Management', add the jenkins-packerbuilds repo link (again, this is your fork's url). You may or may not need to also configure credentials depending if the repo is private. Lastly, proceed in adding a new build step. When adding the new build step, click on the option _**Process Job DSLs**_. 

![image](https://user-images.githubusercontent.com/31086993/105564206-2acdc680-5cef-11eb-9523-26f1f645cc0a.png)

Then select the radio button named, "Use the provided DSL script", and paste in the entire contents of packerbuilds-job-dsl.groovy from the jenkins-packerbuilds repo. Finish by saving this configuration and running a build on the seed job. This should generate the newly 'packerbuilds' job/project.

![image](https://user-images.githubusercontent.com/31086993/105564218-33be9800-5cef-11eb-9e6a-fadb8266e714.png)

From there, there will be some additional setting up (again, assuming you intend on using this in it's current implementation):
- Installing **HashiCorp**'s packer in the directory pointed to by PACKER_EXE_PATH (.env).
  - .env at the moment might have missing values needed for some of its env variables.
- Installing Oracle's VirtualBox.
- Jenkins 'Credentials' will need to be setup, though they may not get used by a build if the jenkins-packerbuilds repo is public or not private. These 'Credentials' will need to be setup in the 'Bindings' section.
- An SMTP server to connect to will also need to be setup, as the job does mail a recipient(s) incase a build fails.


## Installation Notes

- Using forked repos will require changes to the main 'packerbuilds' script in the scripts folder. Look for the vars: OS_BUILD_CONFS_REPO_URL, SHELL_PROVISIONERS_REPO_URL, and PACKER_TEMPLATES_REPO_URL.

## License

See LICENSE.
