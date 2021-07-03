# special makefile variables
.DEFAULT_GOAL := help
.RECIPEPREFIX := >

# recursive variables
# For some reason /bin/sh does not have the 'command' builtin despite it being
# a POSIX requirement, then again one system has /bin as a symlink to '/usr/bin'.
SHELL = /usr/bin/sh
ENVSUBST = envsubst
EXECUTABLES = \
	${ENVSUBST}

# simply expanded variables
# param ==> parameter
PARAMS_CONFIG_FILE_PATH := ./configs/job/parameters/parameters.conf
# TODO(cavcrosby): adding another '/' to the end of the dir will cause make to fail. Maybe integrate pathjoin? (will need to be reimplemented in just sh vs bash).
PARAMS_DIR_PATH := ./configs/job/parameters
# ext ==> extension
PARAM_EXT := .groovy
PARAM_TEMPLATE_EXT := .groovy.shtpl
PARAM_WILDCARD := %${PARAM_EXT}
PARAM_TEMPLATE_WILDCARD := %${PARAM_TEMPLATE_EXT}
PARAM_TEMPLATES := $(shell find ${PARAMS_DIR_PATH} -name *${PARAM_TEMPLATE_EXT})

# Determines the param(s) name to be generated from the template(s).
# Short hand notation for string substitution: $(text:pattern=replacement).
PARAMS := $(PARAM_TEMPLATES:${PARAM_TEMPLATE_WILDCARD}=${PARAM_WILDCARD})

# Certain executables should exist before running. Inspired from:
# https://stackoverflow.com/questions/5618615/check-if-a-program-exists-from-a-makefile#answer-25668869
# e ==> executable
_CHECK_EXECUTABLES := $(foreach e,${EXECUTABLES},$(if $(shell command -v ${e}),pass,$(error "No ${e} in PATH")))

.PHONY: help
help:
	# inspired by the makefiles of the Linux kernel and Mercurial
>	@echo 'Available make targets:'
>	@echo '  all            - runs all targets needed to prepare project to be'
>	@echo '                 translated into a Jenkins job via the Job-DSL plugin.'

# all template(s) should be evaluated when constructing the project
all: ${PARAMS}

# takes template(s) defined in PARAMS_DIR_PATH and creates the respective param(s)
${PARAMS_DIR_PATH}/${PARAM_WILDCARD}: ${PARAMS_DIR_PATH}/${PARAM_TEMPLATE_WILDCARD}
>	. "${PARAMS_CONFIG_FILE_PATH}" && ${ENVSUBST} < "$<" > "$@"

.PHONY: clean
clean:
	# deletes Jenkins job parameter(s), constructed from template(s)
>	rm --force ${PARAMS_DIR_PATH}/*${PARAM_EXT}
