import SwiftUI

struct BlinkViewModifier: ViewModifier {
    let duration: Double
    @State private var isVisible: Bool = false

    func body(content: Content) -> some View {
        content
            .opacity(isVisible ? 0 : 1)
            .animation(.easeOut(duration: duration).repeatForever(), value: isVisible)
            .onAppear {
                withAnimation {
                    isVisible = true
                }
            }
    }
}

extension View {
    func blinking(enabled: Bool = true, duration: Double = 0.75) -> some View {
        Group {
            if enabled {
                self.modifier(BlinkViewModifier(duration: duration))
            } else {
                self
            }
        }
    }
}
