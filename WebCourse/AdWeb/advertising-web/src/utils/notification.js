import {ElMessage} from "element-plus";

export const successMessage = (message) => {
    ElMessage({
        message: message,
        type: 'success',
        plain: true,
    })
}
export const warningMessage = (message) => {
    ElMessage({
        message: message,
        type: 'warning',
        plain: true,
    })
}
export const infoMessage = (message) => {
    ElMessage({
        message: message,
        type: 'info',
        plain: true,
    })
}
export const errorMessage = (message) => {
    ElMessage({
        message: message,
        type: 'error',
        plain: true,
    })
}