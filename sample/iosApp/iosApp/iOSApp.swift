import SwiftUI
import SampleShared

@main
struct iOSApp: App {

    var serviceLocator: ServiceLocator!

    init() {
        KoinKt.doInitKoin()

        // Initialize the shared ServiceLocator after Koin is initialized
        serviceLocator = ServiceLocator()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                // Support link clicks in browser or notes app.
                .onOpenURL { url in
                    serviceLocator.navigator.handleDeepLink(uriString: url.absoluteString)
                }
                // Support link emitted from apps like camera.
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { userActivity in
                    if let url = userActivity.webpageURL {
                        serviceLocator.navigator.handleDeepLink(uriString: url.absoluteString)
                    }
                }
        }
    }
}
