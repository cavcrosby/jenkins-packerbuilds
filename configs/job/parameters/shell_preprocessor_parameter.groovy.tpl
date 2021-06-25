switch (PROJECT){
    // HTML input element formatted according to:
    // https://plugins.jenkins.io/uno-choice/
    // TODO(cavcrosby): perhaps make an importable function that would just take default input value and hidden?
    case "${PROJECT_SHELLFUNCS}":
        return "<input name=\"value\" value=\"shell-processor-bash-shellfuncs\" class=\"setting-input\" type=\"hidden\">"
    case "${NOT_APPLICABLE}":
        return "<input name=\"value\" value=\"\" class=\"setting-input\" type=\"text\">"
}
