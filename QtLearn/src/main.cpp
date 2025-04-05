#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QIcon>

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);

    QQmlApplicationEngine engine;

    // QObject::connect(
    //     &engine,
    //     &QQmlApplicationEngine::objectCreationFailed,
    //     &app,
    //     []() { QCoreApplication::exit(-1); },
    //     Qt::QueuedConnection);

    engine.loadFromModule("MainModule", "Main");

    // can not use "qrc:/xxxxx" ，only ":/xxxxx"
    app.setWindowIcon(QIcon(":/images/favicon.ico"));

    return app.exec();
}
