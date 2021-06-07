switch (OPERATING_SYSTEM){
    case "$OPERATING_SYSTEM_UBUNTU":
        return [
            "$GUEST_OS_TYPE_UBUNTU_64"
	    ]
    case "$OPERATING_SYSTEM_DEBIAN":
        return [
            "$GUEST_OS_TYPE_DEBIAN_64"
        ]
    case "$NOT_APPLICABLE":
        return [
            "$NOT_APPLICABLE"
        ]
}
