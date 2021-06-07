switch (OPERATING_SYSTEM){
    case "$OPERATING_SYSTEM_UBUNTU":
        return [
            "$UBUNTU_18_04_2",
            "${UBUNTU_20_04_1}:selected"
	    ]
    case "$OPERATING_SYSTEM_DEBIAN":
        return [
            "${DEBIAN_10_7_0}:selected"
        ]
    case "$NOT_APPLICABLE":
        return [
            "$NOT_APPLICABLE"
        ]
}
