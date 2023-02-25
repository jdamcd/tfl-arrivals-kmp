import SwiftUI

@main
struct ArrivalsApp: App {
    @NSApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        Settings {
            EmptyView()
        }
    }
}

class AppDelegate: NSObject, NSApplicationDelegate {
    var statusItem: NSStatusItem?
    var popover = NSPopover()

    func applicationDidFinishLaunching(_: Notification) {
        let menuView = ArrivalsBoard()

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
    }
}
