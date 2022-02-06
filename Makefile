# special makefile variables
.DEFAULT_GOAL := help
.RECIPEPREFIX := >

# recursive variables
# For some reason /bin/sh does not have the 'command' builtin despite it being
# a POSIX requirement, then again one system has /bin as a symlink to '/usr/bin'.
SHELL = /usr/bin/sh

# executables
ENVSUBST = envsubst

# targets
ALL = all
CLEAN = clean

# simply expanded variables
PARAMS_CONFIG_FILE_PATH := ./configs/job/parameters/parameters.conf
PARAMS_DIR_PATH := ./configs/job/parameters
PARAM_EXT := .groovy
param_wildcard := %${PARAM_EXT}
SHELL_TEMPLATE_EXT := .shtpl
param_shell_template_ext := ${PARAM_EXT}${SHELL_TEMPLATE_EXT}
param_shell_template_wildcard := %${param_shell_template_ext}
param_shell_templates := $(shell find ${PARAMS_DIR_PATH} -name *${param_shell_template_ext})

# Determines the param(s) name to be generated from the template(s).
# Short hand notation for string substitution: $(text:pattern=replacement).
params := $(param_shell_templates:${param_shell_template_wildcard}=${param_wildcard})

.PHONY: help
help:
	# inspired by the makefiles of the Linux kernel and Mercurial
>	@echo 'Available make targets:'
>	@echo '  ${ALL}            - runs all targets needed to prepare project to be'
>	@echo '                 translated into a Jenkins job via the Job-DSL plugin'
>	@echo '  ${CLEAN}          - removes files generated from other targets'

# all template(s) should be evaluated when constructing the project
${ALL}: ${params}

# takes template(s) defined in PARAMS_DIR_PATH and creates the respective param(s)
${PARAMS_DIR_PATH}/${param_wildcard}: ${PARAMS_DIR_PATH}/${param_shell_template_wildcard}
>	. "${PARAMS_CONFIG_FILE_PATH}" && ${ENVSUBST} < "$<" > "$@"

.PHONY: ${CLEAN}
${CLEAN}:
	# deletes Jenkins job parameter(s), constructed from template(s)
>	rm --force ${PARAMS_DIR_PATH}/*${PARAM_EXT}
