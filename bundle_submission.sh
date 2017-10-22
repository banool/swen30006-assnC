SUBMISSION_FOLDER_NAME="other/implementation_submission_bundle"

mkdir -p $SUBMISSION_FOLDER_NAME
cp -r core/src/mycontroller $SUBMISSION_FOLDER_NAME
cp -r core/src/pathfinders $SUBMISSION_FOLDER_NAME
cp -r core/src/pathfollowers $SUBMISSION_FOLDER_NAME
mkdir -p "$SUBMISSION_FOLDER_NAME/utilities"
cp core/src/utilities/Coordinate.java "$SUBMISSION_FOLDER_NAME/utilities"
mkdir -p "$SUBMISSION_FOLDER_NAME/world"
cp core/src/world/WorldSpatial.java "$SUBMISSION_FOLDER_NAME/world"

echo "Success!"
