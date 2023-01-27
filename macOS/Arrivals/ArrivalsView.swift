import SwiftUI
import TflArrivals
import Foundation

struct ArrivalsView: View {
    
    @ObservedObject var viewModel = ArrivalsViewModel()
        
    var body: some View {
        ZStack {
            switch viewModel.state {
            case .loading :
                ProgressView()
                    .scaleEffect(0.5)
            case let .data(arrivalsList) :
                VStack(spacing: 0) {
                    ArrivalListView(arrivals: arrivalsList)
                    ControlFooter() {
                        viewModel.load()
                    }
                }
            }
        }
        .frame(width: 350, height: 106)
        .onAppear {
            viewModel.load()
        }
    }
}

private struct ArrivalListView: View {

    var arrivals: [Arrival]
    
    var body: some View {
        VStack(spacing: 6) {
            ForEach(arrivals, id: \.id) { arrival in
                HStack {
                    Text(arrival.destination)
                        .font(.custom("LondonUnderground", size: 14))
                            .foregroundColor(.yellow)

                    Spacer()
                    
                    Text(arrival.time)
                        .font(.custom("LondonUnderground", size: 14))
                            .foregroundColor(.yellow)
                }
            }
        }
        .padding(8)
        .background(Color.black)
        .cornerRadius(4)
        .padding(8)
    }
}

private struct ControlFooter: View {
    
    var refresh: () -> Void
    
    var body: some View {
        HStack(spacing: 10){
            Spacer()
            Button {
                refresh()
            } label: {
                Image(systemName: "arrow.clockwise.circle.fill")
            }.buttonStyle(PlainButtonStyle())
                .frame(width: 6, height: 6)
        
            Button {
                NSApp.terminate(self)
            } label: {
                Image(systemName: "x.circle.fill")
            }.buttonStyle(PlainButtonStyle())
                .frame(width: 6, height: 6)
            Spacer()
        }.padding(.bottom, 8)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        let arrivals = [
            Arrival(id: 1, destination: "Clapham Junction", time: "2 min"),
            Arrival(id: 2, destination: "New Cross", time: "7 min"),
            Arrival(id: 3, destination: "Crystal Palace", time: "11 min")
        ]
        
        ArrivalListView(arrivals: arrivals)
    }
}
