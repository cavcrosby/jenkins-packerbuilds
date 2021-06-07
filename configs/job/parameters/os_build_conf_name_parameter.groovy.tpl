switch (PROJECT_NAME){
    // HTML input element formatted according to:
    // https://plugins.jenkins.io/uno-choice/
    case "$PROJECT_SHELLFUNCS":
        return "<input name=\"value\" value=\"debian_preseed.cfg\" class=\"setting-input\" type=\"text\">"
    case "$NOT_APPLICABLE":
        return "<input name=\"value\" value=\"\" class=\"setting-input\" type=\"text\">"
}
