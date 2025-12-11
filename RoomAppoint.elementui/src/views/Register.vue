<template>
  <div class="auth-container">
    <div class="auth-background">
      <div class="auth-overlay">
        <div class="auth-logo">
          <i class="el-icon-user-plus"></i>
          <h1>加入自习室预约</h1>
          <p>开启高效学习之旅</p>
        </div>
      </div>
    </div>
    
    <div class="auth-card">
      <div class="auth-header">
        <h2>创建新账户</h2>
        <p>请填写以下信息完成注册</p>
      </div>
      
      <el-form 
        ref="registerForm" 
        :model="formData" 
        :rules="rules" 
        class="auth-form"
        @keyup.enter="RegisterBtn"
      >
        <div class="form-row">
          <div class="form-group">
            <label>账号 <span class="required">*</span></label>
            <el-form-item prop="UserName">
              <el-input
                v-model.trim="formData.UserName"
                placeholder="请输入账号"
                prefix-icon="el-icon-user"
                size="large"
                clearable
              />
            </el-form-item>
          </div>
          
          <div class="form-group">
            <label>密码 <span class="required">*</span></label>
            <el-form-item prop="Password">
              <el-input
                v-model.trim="formData.Password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                clearable
              />
              <div class="password-hint">
                密码长度1-20位，建议包含字母和数字
              </div>
            </el-form-item>
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>姓名 <span class="required">*</span></label>
            <el-form-item prop="Name">
              <el-input
                v-model.trim="formData.Name"
                placeholder="请输入真实姓名"
                prefix-icon="el-icon-s-custom"
                size="large"
                clearable
              />
            </el-form-item>
          </div>
          
          <div class="form-group">
            <label>邮箱 <span class="required">*</span></label>
            <el-form-item prop="Email">
              <el-input
                v-model.trim="formData.Email"
                placeholder="请输入邮箱"
                prefix-icon="el-icon-message"
                size="large"
                clearable
              />
            </el-form-item>
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>联系方式 <span class="required">*</span></label>
            <el-form-item prop="PhoneNumber">
              <el-input
                v-model.trim="formData.PhoneNumber"
                placeholder="请输入手机号"
                prefix-icon="el-icon-phone"
                size="large"
                clearable
              />
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
        <div class="form-group full-width">
            <label>验证码 <span class="required">*</span></label>
            <el-form-item prop="Code">
            <div class="code-container vertical-layout">
                <el-input
                v-model.trim="formData.Code"
                placeholder="请输入验证码"
                prefix-icon="el-icon-key"
                size="large"
                clearable
                />
                <div class="code-image-wrapper">
                <ValidCode ref="ValidCode" />
                </div>
            </div>
            </el-form-item>
        </div>
        </div>
        
        
        <div class="form-group">
          <label>默认角色</label>
          <el-form-item>
            <el-tag type="info" size="large" class="role-tag">
              普通用户
            </el-tag>
            <div class="role-hint">
              注册成功后，如需其他角色权限请联系管理员
            </div>
          </el-form-item>
        </div>
        
        <div class="agreement">
          <el-checkbox v-model="agreed">
            我已阅读并同意
            <a href="javascript:;" class="link" @click="showAgreement">《用户协议》</a>
            和
            <a href="javascript:;" class="link" @click="showPrivacy">《隐私政策》</a>
          </el-checkbox>
        </div>
        
        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="!agreed"
            @click="RegisterBtn"
            class="register-btn"
          >
            注 册
          </el-button>
        </div>
        
        <div class="auth-footer">
          <span>已有账户？</span>
          <router-link :to="{ path: '/Login' }" class="auth-link">
            <i class="el-icon-user"></i>
            立即登录
          </router-link>
        </div>
        
        
      </el-form>
    </div>
    
    <!-- 协议对话框 -->
    <el-dialog
      title="用户协议"
      :visible.sync="agreementVisible"
      width="50%"
    >
      <div class="agreement-content">
        <h3>自习室预约系统用户协议</h3>
        <p>1. 本协议是您与自习室预约系统之间关于使用本系统所订立的协议...</p>
        <p>2. 您在使用本系统服务时，应遵守相关法律法规...</p>
        <p>3. 您应妥善保管账户信息，不得将其转让或出借给他人使用...</p>
        <!-- 更多协议内容 -->
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="agreementVisible = false">取消</el-button>
        <el-button type="primary" @click="agreeAgreement">同意协议</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ValidCode from '@/components/Code/canvas.vue';

export default {
  name: 'Register',
  components: {
    ValidCode
  },
  data() {
    return {
      formData: {
        UserName: '',
        Password: '',
        Name: '',
        Email: '',
        PhoneNumber: '',
        RoleType: "2",
        Code: ""
      },
      agreed: false,
      loading: false,
      agreementVisible: false,
      privacyVisible: false,
      rules: {
        UserName: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '账号只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        Password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
          { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
        ],
        Name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '姓名长度为2-10个字符', trigger: 'blur' }
        ],
        Email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              const reg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
              if (!value || !reg.test(value)) {
                callback(new Error('请输入正确的邮箱格式'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        PhoneNumber: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              const reg = /^1[3-9]\d{9}$/;
              if (!value || !reg.test(value)) {
                callback(new Error('请输入正确的手机号'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        Code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              let identifyCode = this.$refs.ValidCode?.getCode();
              if (!identifyCode || value !== identifyCode) {
                callback(new Error('验证码不正确'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ]
      }
    };
  },
  methods: {
    async RegisterBtn() {
      try {
        await this.$refs.registerForm.validate();
        
        if (!this.agreed) {
          this.$message.warning('请先阅读并同意用户协议');
          return;
        }
        
        this.loading = true;
        const res = await this.$Post("/User/Register", this.formData);
        
        if (res.Success) {
          this.$message.success({
            message: "注册成功！",
            duration: 2000,
            showClose: true
          });
          
          // 自动填充登录表单
          setTimeout(() => {
            this.$router.push({ 
              path: "/Login",
              query: { 
                username: this.formData.UserName,
                autoFill: true 
              }
            });
          }, 2000);
        } else {
          this.$message.error(res.Message || "注册失败，请稍后重试");
          this.$refs.ValidCode?.refreshCode();
        }
      } catch (error) {
        console.error('注册失败:', error);
        this.$message.error("注册验证不通过");
        this.$refs.ValidCode?.refreshCode();
      } finally {
        this.loading = false;
      }
    },
    
    showAgreement() {
      this.agreementVisible = true;
    },
    
    showPrivacy() {
      this.$message.info("隐私政策对话框功能待实现");
    },
    
    agreeAgreement() {
      this.agreed = true;
      this.agreementVisible = false;
      this.$message.success("已同意用户协议");
    },
    
    quickRegister(type) {
      this.$message.info(`${type === 'wechat' ? '微信' : 'QQ'}注册功能开发中`);
    }
  }
};
</script>

<style scoped>
/* 重用登录页面的基础样式 */
.auth-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.auth-background {
  flex: 1;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  position: relative;
  overflow: hidden;
}

.auth-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.auth-logo {
  text-align: center;
  color: white;
}

.auth-logo i {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.9;
}

.auth-logo h1 {
  font-size: 36px;
  font-weight: 300;
  margin: 0 0 10px 0;
  opacity: 0.95;
}

.auth-logo p {
  font-size: 16px;
  opacity: 0.8;
  font-weight: 300;
}

.auth-card {
  width: 600px;
  background: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 20px rgba(0, 0, 0, 0.1);
  z-index: 1;
  overflow-y: auto;
}

.auth-header {
  margin-bottom: 30px;
  text-align: center;
}

.auth-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
}

.auth-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 500;
}

.code-container.vertical-layout {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: flex-start;
  width: 40%;
}

.code-container.vertical-layout .code-image-wrapper {

  width: 180px;
}

.required {
  color: #f56c6c;
  margin-left: 2px;
}

.password-hint,
.role-hint {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  line-height: 1.4;
}

.role-tag {
  font-size: 16px;
  background: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
}

.code-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.code-image-wrapper {
  width: 120px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
}

.agreement {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.link {
  color: #4facfe;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.form-actions {
  margin: 30px 0 20px 0;
}

.register-btn {
  width: 100%;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 50px;
  border-radius: 8px;
}

.register-btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
  transition: all 0.3s ease;
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin: 20px 0 30px 0;
  color: #666;
  font-size: 14px;
}

.auth-footer .auth-link {
  color: #4facfe;
  text-decoration: none;
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  transition: color 0.3s;
}

.auth-footer .auth-link:hover {
  color: #00f2fe;
  text-decoration: underline;
}

.divider {
  position: relative;
  text-align: center;
  margin: 30px 0;
  color: #999;
  font-size: 14px;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #eee;
  z-index: 0;
}

.divider span {
  position: relative;
  display: inline-block;
  padding: 0 20px;
  background: white;
  z-index: 1;
}

.quick-register {
  text-align: center;
}

.quick-title {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}


.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.agreement-content h3 {
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.agreement-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
  text-align: justify;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-container {
    flex-direction: column;
  }
  
  .auth-background {
    height: 200px;
    flex: none;
  }
  
  .auth-overlay {
    padding: 20px;
  }
  
  .auth-logo i {
    font-size: 50px;
  }
  
  .auth-logo h1 {
    font-size: 24px;
  }
  
  .auth-card {
    width: 100%;
    padding: 30px 20px;
    box-shadow: none;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>

<style>
/* 全局覆盖样式 */
.el-input__inner {
  border-radius: 8px;
}

.el-button {
  border-radius: 8px;
}

.el-radio-button__inner {
  border-radius: 8px !important;
  border: 1px solid #dcdfe6 !important;
  margin: 0 2px;
}

.el-radio-button:first-child .el-radio-button__inner {
  border-radius: 8px 0 0 8px !important;
}

.el-radio-button:last-child .el-radio-button__inner {
  border-radius: 0 8px 8px 0 !important;
}
</style>