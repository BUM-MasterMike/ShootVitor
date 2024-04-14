#
# Package ShootVitor on macOS
#



VERSION=2.0
JPACKAGE=/Volumes/Fastdrive/Development/JDKs/Azul/mac/zulu21.32.17-ca-jdk21.0.2-macosx_x64/bin/jpackage
JMODS=/Users/Mike/Library/Java/javafx-jmods-21.0.2
TYPE=pkg



$JPACKAGE  \
 --type "$TYPE"  \
 --dest "../output"  \
 --name "ShootVitor"  \
 --app-version "$VERSION"  \
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  \
 --vendor "autumo GmbH"  \
 --copyright "2024 autumo GmbH"  \
 --license-file "../package/LICENSE.txt"  \
 --resource-dir "../package"  \
 --icon "../package/ShootVitor.icns"  \
 --module-path "$JMODS"  \
 --add-modules javafx.controls,javafx.media  \
 --main-jar "../target/ShootVitor-$VERSION.jar"  \
 --main-class "ch.autumo.games.shootvitor.App"  \
 --input "../target"  \
 --java-options -Xmx2048m \
 --mac-package-identifier "ch.autumo.games.shootvitor"  \
 --mac-package-name "ShootVitor $VERSION"  \
 --mac-app-category "public.app-category.games"

