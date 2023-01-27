import SwiftUI
import TflArrivals

struct ArrivalsView: View {
    
    @ObservedObject var viewModel = ArrivalsViewModel()
        
    var body: some View {
        ZStack {
            switch viewModel.state {
            case .loading :
                ProgressView()
            case let .data(arrivalsList) :
                VStack {
                    ArrivalListView(arrivals: arrivalsList)
                    Spacer()
                }
            }
        }
        .frame(width: 400, height: 100)
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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        let arrivals = [
            Arrival(id: 1, destination: "Clapham Junction", time: "2mins"),
            Arrival(id: 2, destination: "New Cross", time: "7mins"),
            Arrival(id: 3, destination: "Crystal Palace", time: "11mins")
        ]
        
        ArrivalListView(arrivals: arrivals)
    }
}
