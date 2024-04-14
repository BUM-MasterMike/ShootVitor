#
# Package ShootVitor on Linux
#



VERSION=2.0
JPACKAGE=/opt/java/zulu21.32.17-ca-fx-jdk21.0.2-linux_x64/bin/jpackage
JMODS=/opt/java/zulu21.32.17-ca-fx-jdk21.0.2-linux_x64/jmods
TYPE=deb



$JPACKAGE  \
 --type "$TYPE"  \
 --dest "../output"  \
 --name "ShootVitor"  \
 --app-version "$VERSION"  \
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  \
 --vendor "autumo GmbH"  \
 --copyright "2024 autumo GmbH"  \
 --license-file "../package/LICENSE-OS-Installer.txt"  \
 --resource-dir "../package"  \
 --icon "../package/ShootVitor.png"  \
 --module-path "$JMODS"  \
 --add-modules javafx.controls,javafx.media  \
 --main-jar "target/ShootVitor-$VERSION.jar"  \
 --main-class "ch.autumo.games.shootvitor.App"  \
 --input "../target"  \
 --java-options -Xmx2048m \
 --linux-app-release "$VERSION"  \
 --linux-menu-group "Game"  \
 --linux-app-category "Game"  \
 --linux-deb-maintainer "info@autumo.ch"

