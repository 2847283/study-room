<template>
  <div class="auth-container">
    <div class="auth-background">
      <div class="auth-overlay">
        <div class="auth-logo">
          <i class="el-icon-reading"></i>
          <h1>智能自习室预约系统</h1>
          <p>专注学习，高效预约</p>
        </div>
      </div>
    </div>
    
    <div class="auth-card">
      <div class="auth-header">
        <h2>欢迎回来</h2>
        <p>请登录您的账户以继续</p>
      </div>
      
      <el-form 
        ref="loginForm" 
        :model="formData" 
        :rules="rules" 
        class="auth-form"
        @keyup.enter="LoginBtn"
      >
        <div class="form-group">
          <label>账号</label>
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
          <label>密码</label>
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
          </el-form-item>
        </div>
        
        <div class="form-group">
          <label>角色</label>
          <el-form-item prop="RoleType">
            <el-radio-group v-model="formData.RoleType" class="role-radio-group">
              <el-radio-button
                v-for="item in roleOptions"
                :key="item.Code"
                :label="item.Code"
              >
                {{ item.Label }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
        
        <div class="form-group">
          <label>验证码</label>
          <el-form-item prop="Code">
            <div class="code-container">
              <el-input
                v-model.trim="formData.Code"
                placeholder="请输入验证码"
                prefix-icon="el-icon-key"
                size="large"
                clearable
                style="flex: 1"
              />
              <div class="code-image-wrapper">
                <ValidCode ref="ValidCode" />
              </div>
            </div>
          </el-form-item>
        </div>
        
        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="LoginBtn"
            class="auth-btn"
          >
            登 录
          </el-button>
          
          <el-button
            type="default"
            size="large"
            @click="ToHome"
            class="home-btn"
          >
            <i class="el-icon-s-home"></i>
            前台预览
          </el-button>
        </div>
        
        <div class="auth-footer">
          <router-link :to="{ path: '/Register' }" class="auth-link">
            <i class="el-icon-user-plus"></i>
            立即注册
          </router-link>
          <a href="javascript:;" class="auth-link" @click="forgotPassword">
            <i class="el-icon-question"></i>
            忘记密码
          </a>
        </div>
        
      </el-form>
    </div>
  </div>
</template>

<script>
import ValidCode from '@/components/Code/canvas.vue';
import store from '@/store';

export default {
  name: 'Login',
  components: {
    ValidCode
  },
  data() {
    return {
      formData: {
        UserName: '',
        Password: '',
        RoleType: '',
        Code: ''
      },
      roleOptions: [],
      loading: false,
      rules: {
        UserName: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' }
        ],
        Password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '密码不能为空', trigger: 'blur' }
        ],
        RoleType: [
          { required: true, message: '请选择角色', trigger: 'change' }
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
  created() {
    this.GetRoleTypeApi();
  },
  methods: {
    async GetRoleTypeApi() {
      try {
        const { Data: { Items } } = await this.$Post("/Select/RoleType");
        this.roleOptions = Items;
        // 设置默认角色
        if (Items.length > 0 && !this.formData.RoleType) {
          this.formData.RoleType = Items[0].Code;
        }
      } catch (error) {
        console.error('获取角色类型失败:', error);
      }
    },
    
    async LoginBtn() {
      try {
        await this.$refs.loginForm.validate();
        
        this.loading = true;
        const res = await store.dispatch("Login", this.formData);
        
        if (res.Success) {
          this.$message.success({
            message: "登录成功！",
            duration: 1500,
            showClose: true
          });
          
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            this.$router.push({ path: "/Admin" });
          }, 1500);
        } else {
          this.$message.error(res.Message || "登录失败，请检查账号密码");
          this.$refs.ValidCode?.refreshCode();
        }
      } catch (error) {
        console.error('登录失败:', error);
        this.$message.error("登录验证不通过");
        this.$refs.ValidCode?.refreshCode();
      } finally {
        this.loading = false;
      }
    },
    
    ToHome() {
      this.$router.push({ path: "/Front/Home" });
    },
    
    forgotPassword() {
      this.$message.info("请联系管理员重置密码");
    }
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.auth-background {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.auth-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
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
  width: 500px;
  background: white;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 20px rgba(0, 0, 0, 0.1);
  z-index: 1;
  overflow-y: auto;
}

.auth-header {
  margin-bottom: 40px;
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

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 500;
}

.role-radio-group {
    display: flex;
    flex-direction: row; /* 确保水平排列 */
    gap: 1px; /* 按钮之间的间距 */
}



.role-radio-group .el-radio-button:first-child .el-radio-button__inner {
  border-radius: 4px 0 0 4px;
}

.role-radio-group .el-radio-button:last-child .el-radio-button__inner {
  border-radius: 0 4px 4px 0;
}

.code-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.code-image-wrapper {
  width: 180px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
}

.form-actions {
  margin: 30px 0 20px 0;
  display: flex;
  gap: 15px;
}

.auth-btn {
  flex: 2;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
}

.auth-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.home-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

.auth-footer {
  display: flex;
  justify-content: space-between;
  margin: 20px 0 30px 0;
}

.auth-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.3s;
}

.auth-link:hover {
  color: #764ba2;
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
  
  .form-actions {
    flex-direction: column;
  }
}
</style>