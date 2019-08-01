# This is required because Xatkit is not yet on Maven Central or similar

# Print a message
e() {
    echo -e "$1"
}

main() {
	
    e "Building Xatkit"
    cd /tmp
    git clone https://github.com/xatkit-bot-platform/xatkit.git
    cd xatkit
    mvn install -DskipTests
}

main