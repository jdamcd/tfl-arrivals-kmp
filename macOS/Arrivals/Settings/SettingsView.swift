import SwiftUI
import TflArrivals

struct SettingsView: View {
    let transitSystem = ["TfL", "Custom GTFS"] // TODO: Add MTA
    @State private var selector: String

    init() {
        selector = transitSystem.first!
    }

    var body: some View {
        Form {
            Section {
                Picker("Transit system",
                       selection: $selector) {
                    ForEach(transitSystem, id: \.self) {
                        Text($0)
                    }
                    .pickerStyle(.menu)
                }
            }
            switch selector {
            case "TfL":
                TflSettingsView()
            case "Custom GTFS":
                GtfsSettingsView()
            default:
                GtfsSettingsView()
            }
        }
        .formStyle(.grouped)
        .frame(width: 450, height: 335)
    }
}

#Preview {
    SettingsView()
}
