FILE="${SRCROOT}/Pods/Target Support Files/Pods-expobrownfieldtest-BrownfieldApp/ExpoModulesProvider.swift"

if [ -f "$FILE" ]; then
  echo "Patching $FILE to hide Expo from public interface"
  sed -i 's/^import EX/internal import EX/' "$FILE"
  sed -i 's/^import Ex/internal import Ex/' "$FILE"
  sed -i 's/public class ExpoModulesProvider/internal class ExpoModulesProvider/' "$FILE"
fi
