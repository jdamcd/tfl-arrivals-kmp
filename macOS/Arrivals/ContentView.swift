import SwiftUI
import TflArrivals

struct ContentView: View {
        
    var body: some View {
        VStack {
            Text("TFL Arrivals").font(.custom("LondonUnderground", size: 20))
                .foregroundColor(.yellow)
        }
        .padding()
        .frame(width: 300, height: 100)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
