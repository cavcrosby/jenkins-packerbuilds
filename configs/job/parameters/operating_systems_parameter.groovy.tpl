def criteria = [BUILD_TYPE, PROJECT_NAME]
switch (criteria){
    // it variable in this case is the switch value, for reference:
    // https://groovy-lang.org/semantics.html#_switch_case
    case {it == ["$BUILD_TYPE_BASIC", "$NOT_APPLICABLE"]}:
        return [
            "${OPERATING_SYSTEM_UBUNTU}:selected",
            "$OPERATING_SYSTEM_DEBIAN"
        ]
    case {it == ["$BUILD_TYPE_PROJECT", "$PROJECT_SHELLFUNCS"]}:
        return [
            "${OPERATING_SYSTEM_DEBIAN}:selected",
            "$OPERATING_SYSTEM_UBUNTU"
        ]
}
