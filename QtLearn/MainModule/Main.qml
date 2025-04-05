import QtQuick
import QtQuick.Controls.Basic

Window {
    width: 640
    height: 480
    visible: true
    title: qsTr("Hello World")

    Button {
        id: btn_1
        width: 200
        height: 100
        text: "click me"
        anchors.centerIn: parent
        onClicked: {
            console.log("clicked")
        }
    }

    Image {
        id: img1
        source: "qrc:/images/xixi.png"
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: parent.top
    }

}
