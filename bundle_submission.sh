SUBMISSION_FOLDER_NAME="other/implementation_submission_bundle"

mkdir -p $SUBMISSION_FOLDER_NAME
cp -r core/src/mycontroller $SUBMISSION_FOLDER_NAME
cp -r core/src/pathfinders $SUBMISSION_FOLDER_NAME
cp -r core/src/pathfollowers $SUBMISSION_FOLDER_NAME
mkdir -p "$SUBMISSION_FOLDER_NAME/utilities"
cp -r core/src/utilities/DirectionUtils.java "$SUBMISSION_FOLDER_NAME/utilities"

echo "Success!"
