import SwiftUI

@main
struct ArrivalsApp: App {
    @NSApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @State private var settingsWindow: NSWindow?

    var body: some Scene {
        Settings {
            SettingsView()
                .background(WindowAccessor(window: $settingsWindow))
                .onReceive(NotificationCenter.default.publisher(for: NSWindow.willCloseNotification)) { notif in
                    if let window = notif.object as? NSWindow {
                        print("Window \(window.windowNumber) is closing. Settings is \(settingsWindow?.windowNumber ?? -1).")
                        if window.windowNumber == settingsWindow?.windowNumber {
                            NSApplication.accessoryMode()
                        }
                    }
                }
        }
    }
}

struct WindowAccessor: NSViewRepresentable {
    @Binding var window: NSWindow?

    func makeNSView(context _: Context) -> NSView {
        let view = NSView()
        DispatchQueue.main.async {
            self.window = view.window
        }
        return view
    }

    func updateNSView(_: NSView, context _: Context) {}
}

class PopoverState: ObservableObject {
    @Published var isShown = false
}

class AppDelegate: NSObject, NSApplicationDelegate {
    var statusItem: NSStatusItem?
    var popover = NSPopover()

    @ObservedObject var popoverState = PopoverState()

    func applicationDidFinishLaunching(_: Notification) {
        let menuView = ArrivalsView(popoverState: popoverState)

        popover.behavior = .transient
        popover.animates = true
        popover.contentViewController = NSViewController()
        popover.contentViewController?.view = NSHostingView(rootView: menuView)

        statusItem = NSStatusBar.system.statusItem(withLength: NSStatusItem.variableLength)
        if let menuButton = statusItem?.button {
            menuButton.image = NSImage(systemSymbolName: "tram.fill", accessibilityDescription: nil)
            menuButton.action = #selector(menuButtonToggle)
        }
    }

    @objc func menuButtonToggle(sender: AnyObject) {
        if popover.isShown {
            popover.performClose(sender)
        } else {
            if let menuButton = statusItem?.button {
                popover.show(relativeTo: menuButton.bounds, of: menuButton, preferredEdge: NSRectEdge.minY)
                popover.contentViewController?.view.window?.makeKey()
            }
        }
        popoverState.isShown = popover.isShown
    }
}

extension NSApplication {
    static func foregroundMode() {
        NSApp.setActivationPolicy(.regular)
        NSApp.activate(ignoringOtherApps: true)
    }

    static func accessoryMode() {
        NSApp.setActivationPolicy(.accessory)
    }
}
