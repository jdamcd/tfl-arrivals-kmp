import SwiftUI

struct SettingsView: View {
    @Environment(\.controlActiveState) private var controlActiveState
    @State private var name: String = ""

    var body: some View {
        VStack(alignment: .leading) {
            TextField("Search station", text: $name)
            Text("Station: \(name)")
            Spacer()
        }.padding()
            .frame(width: 300, height: 200)
    }
}

struct Settings_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
