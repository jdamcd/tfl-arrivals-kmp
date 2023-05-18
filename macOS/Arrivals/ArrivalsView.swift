import Foundation
import SwiftUI
import TflArrivals

struct ArrivalsView: View {
    @ObservedObject var viewModel = ArrivalsViewModel()
    @ObservedObject var popoverState: PopoverState

    var body: some View {
        let refreshBehaviour = Refresh(isLoading: viewModel.loading) {
            viewModel.load()
        }
        ZStack {
            switch viewModel.state {
            case .idle:
                ProgressView()
                    .scaleEffect(0.5)
            case .error:
                MainDisplay(footerText: "Refresh →", refreshBehaviour: refreshBehaviour) {
                    DotMatrixRow(leadingText: "Error fetching arrivals", trailingText: "")
                }
            case let .data(arrivalsInfo):
                MainDisplay(footerText: arrivalsInfo.station, refreshBehaviour: refreshBehaviour) {
                    VStack(spacing: 6) {
                        ForEach(arrivalsInfo.arrivals, id: \.id) { arrival in
                            DotMatrixRow(leadingText: arrival.destination, trailingText: arrival.time)
                        }
                    }
                }
            }
        }
        .padding(8)
        .frame(width: 350, height: 110)
        .onAppear {
            viewModel.load()
        }
        .onReceive(popoverState.$isShown) { isShown in
            if isShown {
                viewModel.load()
            }
        }
    }
}

private struct MainDisplay<Content: View>: View {
    var footerText: String
    var refreshBehaviour: Refresh
    @ViewBuilder var content: Content

    var body: some View {
        VStack {
            ZStack { content }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(8)
                .background(Color.black)
                .cornerRadius(4)
            ControlFooter(text: footerText, refresh: refreshBehaviour)
        }
    }
}

private struct ControlFooter: View {
    var text: String
    var refresh: Refresh

    var body: some View {
        HStack(spacing: 2) {
            Text(text)
                .font(.footnote)
                .foregroundColor(Color.gray)
                .padding(.leading, 2)
            Spacer()
            Button {
                openSettings()
            } label: {
                Image(systemName: "gearshape.circle.fill")
                    .foregroundColor(Color.yellow)
            }.buttonStyle(PlainButtonStyle())
            Button {
                refresh.onRefresh()
            } label: {
                Image(systemName: "arrow.clockwise.circle.fill")
                    .foregroundColor(refresh.isLoading ? Color.gray : Color.yellow)
            }.buttonStyle(PlainButtonStyle())
                .disabled(refresh.isLoading)
            Button {
                NSApp.terminate(self)
            } label: {
                Image(systemName: "x.circle.fill")
                    .foregroundColor(Color.yellow)
            }.buttonStyle(PlainButtonStyle())
        }
    }
}

private struct DotMatrixRow: View {
    var leadingText: String
    var trailingText: String

    var body: some View {
        HStack {
            DotMatrixText(text: leadingText)
            Spacer()
            DotMatrixText(text: trailingText)
        }
    }
}

private struct DotMatrixText: View {
    var text: String

    var body: some View {
        Text(text)
            .font(.custom("LondonUnderground", size: 14))
            .foregroundColor(.yellow)
    }
}

private struct Refresh {
    var isLoading: Bool
    var onRefresh: () -> Void
}

private func openSettings() {
    NSApplication.foregroundMode()
    if #available(macOS 13, *) {
        NSApp.sendAction(Selector(("showSettingsWindow:")), to: nil, from: nil)
    } else {
        NSApp.sendAction(Selector(("showPreferencesWindow:")), to: nil, from: nil)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ArrivalsView(popoverState: PopoverState())
        ControlFooter(text: "Station Name", refresh: Refresh(isLoading: false, onRefresh: {}))
    }
}
