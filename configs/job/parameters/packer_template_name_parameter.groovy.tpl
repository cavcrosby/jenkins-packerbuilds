switch (PROJECT_NAME){
    // HTML input element formatted according to:
    // https://plugins.jenkins.io/uno-choice/
    case "$PROJECT_SHELLFUNCS":
        // assuming the same template is used each time
        return "<input name=\"value\" value=\"debian_shellfuncs_preseed_build.json\" class=\"setting-input\" type=\"hidden\">"
    case "$NOT_APPLICABLE":
        return "<input name=\"value\" value=\"\" class=\"setting-input\" type=\"text\">"
}
