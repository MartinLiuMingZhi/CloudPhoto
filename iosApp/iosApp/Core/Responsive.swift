import SwiftUI

/**
 * 响应式配置
 */
struct ResponsiveConfig {
    let deviceType: DeviceType
    let screenSize: ScreenSize
    let orientation: ScreenOrientation
    let screenWidth: CGFloat
    let screenHeight: CGFloat
    
    var isTablet: Bool { deviceType == .tablet }
    var isPhone: Bool { deviceType == .phone }
    var isLandscape: Bool { orientation == .landscape }
    var isPortrait: Bool { orientation == .portrait }
}

enum DeviceType {
    case phone
    case tablet
    case desktop
}

enum ScreenOrientation {
    case portrait
    case landscape
}

enum ScreenSize {
    case small
    case medium
    case large
    case extraLarge
}

/**
 * 响应式环境值
 */
struct ResponsiveConfigKey: EnvironmentKey {
    static let defaultValue: ResponsiveConfig = ResponsiveConfig(
        deviceType: .phone,
        screenSize: .small,
        orientation: .portrait,
        screenWidth: 375,
        screenHeight: 667
    )
}

extension EnvironmentValues {
    var responsiveConfig: ResponsiveConfig {
        get { self[ResponsiveConfigKey.self] }
        set { self[ResponsiveConfigKey.self] = newValue }
    }
}

/**
 * 响应式容器视图
 */
struct ResponsiveContainer<Content: View>: View {
    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @Environment(\.verticalSizeClass) var verticalSizeClass
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        GeometryReader { geometry in
            let config = createConfig(from: geometry)
            content
                .environment(\.responsiveConfig, config)
        }
    }
    
    private func createConfig(from geometry: GeometryProxy) -> ResponsiveConfig {
        let width = geometry.size.width
        let height = geometry.size.height
        
        let deviceType: DeviceType = (horizontalSizeClass == .regular && verticalSizeClass == .regular) ? .tablet : .phone
        let orientation: ScreenOrientation = width > height ? .landscape : .portrait
        
        let screenSize: ScreenSize = {
            let minDimension = min(width, height)
            switch minDimension {
            case ..<600:
                return .small
            case ..<840:
                return .medium
            case ..<1200:
                return .large
            default:
                return .extraLarge
            }
        }()
        
        return ResponsiveConfig(
            deviceType: deviceType,
            screenSize: screenSize,
            orientation: orientation,
            screenWidth: width,
            screenHeight: height
        )
    }
}

/**
 * 响应式配置访问器
 */
extension View {
    func responsiveConfig() -> ResponsiveConfig {
        Environment(\.responsiveConfig).wrappedValue
    }
}

