import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "tram.fill")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("TFL Arrivals")
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
