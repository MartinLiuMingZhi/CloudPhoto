import SwiftUI

/**
 * Toast 消息类型
 */
enum ToastType {
    case info
    case success
    case warning
    case error
}

/**
 * Toast 管理器
 */
class ToastManager {
    static func show(message: String, type: ToastType = .info) {
        // iOS 可以使用第三方库或自定义实现
        // 这里提供一个简单的实现思路
        print("Toast: \(message)")
    }
}

/**
 * Toast 视图修饰符
 */
struct ToastModifier: ViewModifier {
    @Binding var message: String?
    let type: ToastType
    
    func body(content: Content) -> some View {
        content
            .overlay(
                Group {
                    if let message = message {
                        VStack {
                            Spacer()
                            Text(message)
                                .padding()
                                .background(Color.black.opacity(0.7))
                                .foregroundColor(.white)
                                .cornerRadius(8)
                                .padding()
                                .transition(.move(edge: .bottom))
                        }
                    }
                }
                .animation(.default, value: message)
            )
    }
}

extension View {
    func toast(message: Binding<String?>, type: ToastType = .info) -> some View {
        modifier(ToastModifier(message: message, type: type))
    }
}

