import SwiftUI
import TflArrivals
import Foundation

struct ArrivalsBoard: View {
    @ObservedObject var viewModel = ArrivalsViewModel()
        
    var body: some View {
        ZStack {
            switch viewModel.state {
            case .loading:
                ProgressView()
                    .scaleEffect(0.5)
            case .error:
                MainDisplay(footerText: "Refresh >>>", refresh: { viewModel.load() }) {
                    DotMatrixRow(leadingText: "Error fetching arrivals", trailingText: "")
                }
            case let .data(arrivalsInfo):
                MainDisplay(footerText: arrivalsInfo.station, refresh: { viewModel.load() }) {
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
    }
}

private struct MainDisplay<Content: View>: View {
    var footerText: String
    var refresh: () -> Void
    @ViewBuilder var content: Content
    
    var body: some View {
        VStack {
            ZStack { content }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(8)
                .background(Color.black)
                .cornerRadius(4)
            ControlFooter(text: footerText, refresh: refresh)
        }
    }
}

private struct ControlFooter: View {
    var text: String
    var refresh: () -> Void
    
    var body: some View {
        HStack(spacing: 2) {
            Text(text)
                .font(.footnote)
                .foregroundColor(Color.gray)
                .padding(.leading, 2)
            Spacer()
            Button {
                refresh()
            } label: {
                Image(systemName: "arrow.clockwise.circle.fill")
                    .foregroundColor(Color.yellow)
            }.buttonStyle(PlainButtonStyle())
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

struct ContentView_Previews: PreviewProvider {
    
    static var previews: some View {
        ArrivalsBoard()
        ControlFooter(text: "Station Name", refresh: {})
    }
}
