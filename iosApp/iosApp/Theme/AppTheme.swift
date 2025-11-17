import SwiftUI
import Shared

/**
 * iOS 主题配置
 */
struct AppTheme {
    static let lightColors = ColorScheme.light
    static let darkColors = ColorScheme.dark
    
    // 主题颜色
    struct Colors {
        static let primary = Color(red: 0.38, green: 0.0, blue: 0.93)
        static let secondary = Color(red: 0.01, green: 0.85, blue: 0.78)
        static let background = Color.white
        static let surface = Color.white
        static let error = Color(red: 0.69, green: 0.0, blue: 0.13)
    }
}

/**
 * 主题视图修饰符
 */
struct ThemedView<Content: View>: View {
    @AppStorage("themeMode") private var themeMode: String = "system"
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        content
            .preferredColorScheme(colorScheme)
    }
    
    private var colorScheme: ColorScheme? {
        switch themeMode {
        case "light":
            return .light
        case "dark":
            return .dark
        default:
            return nil // 系统默认
        }
    }
}

