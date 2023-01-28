import SwiftUI
import TflArrivals
import Foundation

struct ArrivalsBoard: View {
    @ObservedObject var viewModel = ArrivalsViewModel()
        
    var body: some View {
        ZStack {
            switch viewModel.state {
            case .loading :
                ProgressView()
                    .scaleEffect(0.5)
            case let .data(arrivals) :
                VStack {
                    NextArrivalsList(arrivals: arrivals)
                        .padding(.bottom, 4)
                    ControlFooter() {
                        viewModel.load()
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

private struct NextArrivalsList: View {
    var arrivals: [Arrival]
    
    var body: some View {
        VStack(spacing: 6) {
            ForEach(arrivals, id: \.id) { arrival in
                HStack {
                    DotMatrixText(text: arrival.destination)
                    Spacer()
                    DotMatrixText(text: arrival.time)
                }
            }
        }
        .padding(8)
        .background(Color.black)
        .cornerRadius(4)
    }
}

private struct ControlFooter: View {
    var refresh: () -> Void
    
    var body: some View {
        HStack(spacing: 2){
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
        let arrivals = [
            Arrival(id: 1, destination: "Clapham Junction", time: "2 min"),
            Arrival(id: 2, destination: "New Cross", time: "7 min"),
            Arrival(id: 3, destination: "Crystal Palace", time: "11 min")
        ]
        
        ArrivalsBoard()
        ControlFooter(refresh: {})
        NextArrivalsList(arrivals: arrivals)
    }
}
