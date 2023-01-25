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
        .frame(width: 300, height: 100)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
