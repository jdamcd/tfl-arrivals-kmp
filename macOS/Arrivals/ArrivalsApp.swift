import SwiftUI

@main
struct ArrivalsApp: App {
    
    var body: some Scene {
        MenuBarExtra("Arrivals", systemImage: "tram.fill") {
            // TODO
            Divider()
            Button("Settings") {
                // TODO
            }
            Button("Quit") {
                NSApplication.shared.terminate(nil)
            }.keyboardShortcut("q")
        }
    }
}
