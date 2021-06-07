if(OPERATING_SYSTEM == "$NOT_APPLICABLE") {
    return ["$NOT_APPLICABLE"]
}
switch (OPERATING_SYSTEM_VERSION){
    case "$UBUNTU_18_04_2":
        return [
            "$UBUNTU_18_04_2_ISO_1"
	    ]
    case "$UBUNTU_20_04_1":
        return [
            "$UBUNTU_20_04_1_ISO_1"
	    ]
    case "$DEBIAN_10_7_0":
        return [
            "$DEBIAN_10_7_0_ISO_1"
        ]
}
