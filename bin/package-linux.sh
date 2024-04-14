
cd ..

/opt/java/zulu21.32.17-ca-fx-jdk21.0.2-linux_x64/bin/jpackage  \
 --type deb  \
 --dest output  \
 --name "ShootVitor"  \
 --app-version "2.0"  \
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  \
 --vendor "autumo GmbH"  \
 --copyright "2024 autumo GmbH"  \
 --license-file "package/LICENSE-OS-Installer.txt"  \
 --resource-dir package  \
 --icon "package/ShootVitor.png"  \
 --module-path "/opt/java/zulu21.32.17-ca-fx-jdk21.0.2-linux_x64/jmods":"/home/mike/ShootVitor/target/classes"  \
 --add-modules javafx.controls,javafx.media  \
 --main-jar "/home/mike/ShootVitor/target/ShootVitor-2.0.jar"  \
 --main-class "ch.autumo.games.shootvitor.App"  \
 --input "/home/mike/ShootVitor/target"  \
 --java-options -Xmx2048m \
 --linux-app-release "2.0"  \
 --linux-menu-group "Game"  \
 --linux-app-category "Game"  \
 --linux-deb-maintainer "info@autumo.ch"
